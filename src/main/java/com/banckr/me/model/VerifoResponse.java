package com.banckr.me.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VerifoResponse {
  private VerifoData data;
  private boolean success;
}
