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
public class WalletResponse {
  private VerifoResponseData data;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class VerifoResponseData {
    private String count;
    private String pages;
    private List<Wallet> rows;
  }
}
