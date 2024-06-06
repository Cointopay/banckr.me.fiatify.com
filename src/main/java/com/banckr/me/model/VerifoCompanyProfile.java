package com.banckr.me.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VerifoCompanyProfile {
  private Company data;
  private boolean success;
}
