package com.banckr.me.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Wallet {
  private String walletId;
  private String iban;
  private String name;
  private String status;
  private List<Currency> currencies;
}
