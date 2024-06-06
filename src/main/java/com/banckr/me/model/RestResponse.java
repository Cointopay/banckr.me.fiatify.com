package com.banckr.me.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse<T> {

  private int status;
  private String message;
  private T data;

  public RestResponse(int status, String message) {
    this.status = status;
    this.message = message;
  }

  public RestResponse(int status, T data) {
    this.status = status;
    this.data = data;
  }

}
