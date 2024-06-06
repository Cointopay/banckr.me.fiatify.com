package com.banckr.me.service;

import org.springframework.beans.factory.annotation.Value;

public class BaseService {
  @Value("${verifo.api.version}")
  protected String apiVersion;
  @Value("${verifo.api.key}")
  protected String apiKey;
  @Value("${verifo.api.secret}")
  protected String apiSecret;
  @Value("${verifo.api.url}")
  protected String apiUrl;
}
