package com.banckr.me.controllers;

import com.banckr.me.model.*;
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
import java.util.Optional;

@Controller
@RequestMapping(path = "/")
public class ProcessPaymentsController {

  @Autowired
  private ProcessPaymentService processPaymentService;

  @GetMapping(value = {"{id}", "{id}/{amount}/{msg}"})
  public String showPaymentForm(@PathVariable("id") String walletId, @PathVariable(required = false) String amount, @PathVariable(required = false) String msg, Model model)
          throws NoSuchAlgorithmException, InvalidKeyException {
    int index = walletId.lastIndexOf("-");
    String profileId = walletId.substring(0, index);
    Optional<Account> accountOptional = this.processPaymentService.getAccountByProfileId(profileId);
    if (accountOptional.isPresent()) {
      Account account = accountOptional.get();
      String name = "";
      if (account.getType().equals("person")) {
        ResponseEntity<VerifoPersonProfile> verifoPersonProfile = this.processPaymentService.getPersonalProfile(profileId);
        Person person = Objects.requireNonNull(verifoPersonProfile.getBody()).getData();
        if (person != null) {
          name = person.getFullName();
        }
      } else {
        ResponseEntity<VerifoCompanyProfile> companyProfile = this.processPaymentService.getCompanyProfile(profileId);
        Company company = Objects.requireNonNull(companyProfile.getBody()).getData();
        if (company != null) {
          name = company.getFullName();
        }
      }
      Wallet wallet = this.processPaymentService.getWallet(profileId, walletId);

      if (wallet != null) {
        model.addAttribute("amount", amount == null ? "" : amount);
        model.addAttribute("name", !name.equals("") ? name : wallet.getName());
        model.addAttribute("iban", wallet.getIban());
        model.addAttribute("msg", msg == null ? "" : msg);
        return "payment";
      }
    }
    model.addAttribute("error", "Profile does not exists");
    return "error";
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
