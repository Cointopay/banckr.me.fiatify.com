package com.banckr.me.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class UtilService {

  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
  private static final String HMAC_SHA512 = "HmacSHA512";

  @Value("${salt}")
  private String salt;

  /**
   * Generate Signature
   *
   * @param endPoint  : String
   * @param body      : String
   * @param apiSecret : String
   * @return String
   * @throws InvalidKeyException
   * @throws NoSuchAlgorithmException
   */
  public String generateSignature(String endPoint, String body, String apiSecret)
          throws InvalidKeyException, NoSuchAlgorithmException {
    String data = endPoint + "|" + body;
    SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(), HMAC_SHA512);
    Mac mac = Mac.getInstance(HMAC_SHA512);
    mac.init(secretKeySpec);
    return bytesToHex(mac.doFinal(data.getBytes()));
  }

  /**
   * Byte to hex
   *
   * @param bytes: byte[]
   * @return String
   */
  public String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  public String hash(String text) throws NoSuchAlgorithmException, InvalidKeySpecException {
    SecureRandom random = new SecureRandom();
    random.nextBytes(salt.getBytes());
    KeySpec spec = new PBEKeySpec(text.toCharArray(), salt.getBytes(), 65536, 128);
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    byte[] hash = factory.generateSecret(spec).getEncoded();
    return Base64.getEncoder().encodeToString(hash);
  }

}
