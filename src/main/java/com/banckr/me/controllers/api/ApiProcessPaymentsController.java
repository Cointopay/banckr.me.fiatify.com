package com.banckr.me.controllers.api;

import com.banckr.me.model.*;
import com.banckr.me.service.ProcessPaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api")
public class ApiProcessPaymentsController {

  @Autowired
  private ProcessPaymentService processPaymentService;

  @PostMapping(path = "/process-payment")
  public ResponseEntity<Object> processPayment(@Valid @RequestBody PaymentRequest paymentRequest)
          throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    // Authenticate user account
    Account account = this.processPaymentService.authenticate(paymentRequest.getEmail(), paymentRequest.getPassword());
    if (account != null) {
      // Check if 2FA is enabled
      if (account.is2faEnabled() && paymentRequest.getGoogle2FACode() == null) {
        Map<String, Boolean> payload = new HashMap<>();
        payload.put("is2FAEnabled", true);
        return new ResponseEntity<>(new RestResponse<>(HttpStatus.OK.value(), "", payload), HttpStatus.OK);
      } else {
        // Verify google 2FA code
        if (!paymentRequest.getGoogle2FACode().equals(this.getTOTPCode(account.getTotpSecretKey()))) {
          return  new ResponseEntity<>(new RestResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid Google 2FA Code"), HttpStatus.BAD_REQUEST);
        }
      }
      return this.process(paymentRequest, account);
    } else {
      return new ResponseEntity<>(new RestResponse<>(HttpStatus.NOT_FOUND.value(), "Invalid Credentials!"), HttpStatus.NOT_FOUND);
    }
  }

  public ResponseEntity<Object> process(PaymentRequest paymentRequest, Account account)
          throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    ResponseEntity<WalletResponse> walletList = this.processPaymentService.getAccountWallets(account.getProfileId());
    List<Wallet> wallets = Objects.requireNonNull(walletList.getBody()).getData().getRows();
    Wallet wallet = null;
    if (!wallets.isEmpty()) {
      for (Wallet w : wallets) {
        if (!w.getCurrencies().isEmpty()) {
          for (Currency c : w.getCurrencies()) {
            double balance = Double.parseDouble(c.getBalance());
            if (c.getCurrency().equalsIgnoreCase("eur") && balance >= Double.parseDouble(paymentRequest.getAmount())) {
              wallet = w;
              break;
            }
          }
        }
        if (wallet != null) {
          break;
        }
      }
    }
    if (wallet != null) {
      // Create transaction
      TransactionRequest transactionRequest = new TransactionRequest();
      transactionRequest.setAmount(Double.parseDouble(paymentRequest.getAmount()));
      transactionRequest.setCurrency("EUR");
      transactionRequest.setDetails(paymentRequest.getDetails());
      transactionRequest.setSender(new TransactionRequest.Sender(wallet.getWalletId()));
      transactionRequest.setRecipient(new TransactionRequest.Recipient(
              paymentRequest.getName(),
              paymentRequest.getIban()
      ));
      VerifoResponse verifoResponse = this.processPaymentService.createTransaction(transactionRequest);
      String error = "Internal Server Error";
      if (verifoResponse.isSuccess()) {
        String uuid = verifoResponse.getData().getUuid();
        String status;
        Object operation;
        do {
          ResponseEntity<VerifoResponse> responseEntity = this.processPaymentService.getOperationDetail(uuid);
          status = Objects.requireNonNull(responseEntity.getBody()).getData().getStatus();
          operation = responseEntity.getBody().getData().getOperation();
        } while(!status.equals("failed") && operation == null);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(operation);
        JsonNode jsonNode = mapper.readTree(json);
        if (jsonNode.get("error") != null) {
          error = jsonNode.get("error").asText();
        }
        if (!status.equals("failed")) {
          return new ResponseEntity<>(new RestResponse<>(HttpStatus.OK.value(), "Payment successful", operation), HttpStatus.OK);
        }
      }
      return new ResponseEntity<>(new RestResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), error), HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      return new ResponseEntity<>(new RestResponse<>(HttpStatus.BAD_REQUEST.value(), "Insufficient balance"), HttpStatus.BAD_REQUEST);
    }
  }

  public String getTOTPCode(String secretKey) {
    Base32 base32 = new Base32();
    byte[] bytes = base32.decode(secretKey);
    String hexKey = Hex.encodeHexString(bytes);
    return TOTP.getOTP(hexKey);
  }

}
