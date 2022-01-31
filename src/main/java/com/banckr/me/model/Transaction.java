package com.banckr.me.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {
  private String transactionId;
  private String walletId;
  private String amount;
  private String currency;
  private String dateAuthorized;
  private String dateSettled;
  private String details;
  private String direction;
  private String fee;
  private String service;
  private String status;
  private String total;
  private AdditionalData additionalData;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  private static class AdditionalData {
    private Bank beneficiaryAccountBank;
    private String beneficiaryAccountHolder;
    private String beneficiaryAccountNumber;
    private Bank senderAccountBank;
    private String senderAccountHolder;
    private String senderAccountNumber;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  private static class Bank {
    private String bic;
    private String name;
  }
}

