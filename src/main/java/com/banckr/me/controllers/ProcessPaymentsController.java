package com.banckr.me.controllers;

import com.banckr.me.model.Transaction;
import com.banckr.me.model.TransactionResponse;
import com.banckr.me.model.Wallet;
import com.banckr.me.service.ProcessPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

@Controller
@RequestMapping(path = "/")
public class ProcessPaymentsController {

  @Autowired
  private ProcessPaymentService processPaymentService;

  @GetMapping(value = { "{id}", "{id}/{amount}/{msg}"})
  public String showPaymentForm(@PathVariable("id") String id, @PathVariable(required = false) String amount, @PathVariable(required = false) String msg, Model model) {
    Wallet wallet = this.processPaymentService.getWallet(id);
    if (wallet != null) {
      model.addAttribute("amount", amount == null ? "" : amount);
      model.addAttribute("name", wallet.getName());
      model.addAttribute("iban", wallet.getIban());
      model.addAttribute("msg", msg == null ? "" : msg);
      return "payment";
    } else {
      model.addAttribute("error", "Profile does not exists");
      return "error";
    }
  }

  @GetMapping(path = "/payment/success/{walletId}/{transactionId}")
  public String paymentSuccessful(@PathVariable("walletId") String walletId, @PathVariable("transactionId") String transactionId, Model model)
          throws NoSuchAlgorithmException, InvalidKeyException {
    ResponseEntity<TransactionResponse> responseEntity = this.processPaymentService.getTransaction(walletId, transactionId);
    Transaction t = Objects.requireNonNull(responseEntity.getBody()).getData();
    String[] statusList = new String[]{
            "created",
            "pending",
            "system_approved",
            "manual_approval",
            "processed",
            "completed"
    };
    String status = Arrays.stream(statusList).filter(s -> s.equalsIgnoreCase(t.getStatus())).findFirst().orElse(null);
    if (status == null) {
      model.addAttribute("error", "Oops! something went wrong...");
      return "error";
    } else {
      model.addAttribute("message", "Payment successful");
      return "success";
    }
  }
}
