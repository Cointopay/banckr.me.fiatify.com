package com.banckr.me.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Company {
  private String profileId;
  private String fullName;
  private String email;
  private String phone;
  private String role;
  private boolean verified;
  private String status;
}
