package com.banckr.me.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifoData {
  private String uuid;
  private String remoteId;
  private String type;
  private String status;
  private long timestampCreated;
  private Object operation;
}
