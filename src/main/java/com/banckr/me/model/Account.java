package com.banckr.me.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Account implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @NotNull
  @Email
  private String email;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String token;
  private String totpSecretKey;
  private String profileId;
  private String status;
  @NotNull
  private String type;
  private String app;
  private String ip;
  @Column(name = "is_2fa_enabled")
  private boolean is2faEnabled;
  private boolean isCloseRequested;
  private boolean isFromUsa;
  private boolean termsAccepted;
  private boolean privacyAccepted;
  private boolean newsletterSubscription;
}