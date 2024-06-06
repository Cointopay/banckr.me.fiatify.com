package com.banckr.me.model;

import com.sun.istack.NotNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionRequest {
  @NotNull
  private String amount;
  @NotNull
  private String currency;
  @NotNull
  private String details;
  @NotNull
  private Sender sender;
  @NotNull
  private Recipient recipient;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class Sender {
    @NotNull
    private String walletId;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class Recipient {
    @NotNull
    private String fullName;
    @NotNull
    private String accountNumber;
  }

  @NotNull
  public void setAmount(double amount) {
    this.amount = String.format("%.2f", amount);
  }
}
