package com.banckr.me.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentRequest {
  @NotNull
  @NotEmpty
  private String details;
  @NotNull
  @NotEmpty
  private String amount;
  @NotNull
  @NotEmpty
  @Email
  private String email;
  @NotNull
  @NotEmpty
  private String password;
  @NotNull
  @NotEmpty
  private String name;
  @NotNull
  @NotEmpty
  private String iban;

  private String google2FACode;
}
