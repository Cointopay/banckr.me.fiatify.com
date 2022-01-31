package com.banckr.me.service;

import com.banckr.me.model.*;
import com.banckr.me.repository.AccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProcessPaymentService extends BaseService {

  private final RestTemplate restTemplate;
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private UtilService utilService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public ProcessPaymentService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public Account authenticate(String email, String password) {
    Account account = null;
    try {
      password = this.utilService.hash(password);
      Optional<Account> accountOptional = this.accountRepository.findAccount(email, password);
      if (accountOptional.isPresent()) {
        account = accountOptional.get();
        if ((account.getPassword() == null || account.getProfileId() == null) && account.getStatus().equals("pending")) {
          account = null;
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
    return account;
  }

  public Wallet getWallet(String id) {
    Optional<Account> accountOptional = this.getAccountByProfileId(id);
    Wallet wallet = null;
    if (accountOptional.isPresent()) {
      try {
        ResponseEntity<WalletResponse> walletResponse = this.getAccountWallets(id);
        if (Objects.requireNonNull(walletResponse.getBody()).getData() != null) {
          List<Wallet> walletList = walletResponse.getBody().getData().getRows();
          String walletId = id + "-00"; // Primary wallet
          wallet = walletList.stream().filter(w -> w.getWalletId().equals(walletId)).findFirst().orElse(null);
        }
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
      }
    }
    return wallet;
  }

  public Optional<Account> getAccountByProfileId(String profileId) {
    try {
      return this.accountRepository.findAccountByProfileId(profileId);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Get account wallets
   *
   * @param id String
   * @return ResponseEntity<WalletResponse>
   * @throws NoSuchAlgorithmException RuntimeException
   * @throws InvalidKeyException      RuntimeException
   */
  public ResponseEntity<WalletResponse> getAccountWallets(String id)
          throws NoSuchAlgorithmException, InvalidKeyException {
    String endPoint = "/profile/" + id + "/account/page";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    long nonce = System.currentTimeMillis() * 100;
    long timestamp = (System.currentTimeMillis() / 1000) + 60;
    String apiEndPoint = "/api/" + apiVersion + endPoint + "?nonce=" + nonce + "&timestamp=" + timestamp;
    String signature = this.utilService.generateSignature(apiEndPoint, "", apiSecret);
    String url = apiUrl + apiEndPoint;
    String authHeader = apiVersion.toUpperCase() + "-HMAC-SHA512 Key=" + apiKey + ",Signature=" + signature;
    headers.set("Authorization", authHeader);
    HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
    return this.restTemplate.exchange(url, HttpMethod.GET, httpEntity, WalletResponse.class);
  }

  /**
   * Create transaction
   *
   * @param transactionRequest
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws JsonProcessingException
   */
  public VerifoResponse createTransaction(TransactionRequest transactionRequest)
          throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
    String endPoint = "/account/" + transactionRequest.getSender().getWalletId() + "/transaction";
    HttpHeaders headers = new HttpHeaders();
    String payload = objectMapper.writeValueAsString(transactionRequest);
    headers.setContentType(MediaType.APPLICATION_JSON);
    long nonce = System.currentTimeMillis() * 100;
    long timestamp = (System.currentTimeMillis() / 1000) + 60;
    String apiEndPoint = "/api/" + apiVersion + endPoint + "?nonce=" + nonce + "&timestamp=" + timestamp;
    String signature = this.utilService.generateSignature(apiEndPoint, payload, apiSecret);
    String url = apiUrl + apiEndPoint;
    String authHeader = apiVersion.toUpperCase() + "-HMAC-SHA512 Key=" + apiKey + ",Signature=" + signature;
    headers.set("Authorization", authHeader);
    HttpEntity<String> httpEntity = new HttpEntity<>(payload, headers);
    return this.restTemplate.postForObject(url, httpEntity, VerifoResponse.class);
  }

  /**
   * Get operation detail
   * @param uuid
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  public ResponseEntity<VerifoResponse> getOperationDetail(String uuid)
          throws NoSuchAlgorithmException, InvalidKeyException {
    String endPoint = "/operation";
    HttpHeaders headers = new HttpHeaders();
    long nonce = System.currentTimeMillis() * 100;
    long timestamp = (System.currentTimeMillis() / 1000) + 6;
    String apiEndPoint = "/api/" + apiVersion + endPoint + "?nonce=" + nonce + "&timestamp=" + timestamp + "&uuid=" + uuid;
    String signature = this.utilService.generateSignature(apiEndPoint, "", apiSecret);
    String url = apiUrl + apiEndPoint;
    String authHeader = apiVersion.toUpperCase() + "-HMAC-SHA512 Key=" + apiKey + ",Signature=" + signature;
    headers.set("Authorization", authHeader);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    return this.restTemplate.exchange(url, HttpMethod.GET, request, VerifoResponse.class);
  }

  public ResponseEntity<TransactionResponse> getTransaction(String walletId, String transactionId)
          throws NoSuchAlgorithmException, InvalidKeyException {
    String endPoint = "/account/" + walletId + "/transaction/" + transactionId;
    HttpHeaders headers = new HttpHeaders();
    long nonce = System.currentTimeMillis() * 100;
    long timestamp = (System.currentTimeMillis() / 1000) + 6;
    String apiEndPoint = "/api/" + apiVersion + endPoint + "?nonce=" + nonce + "&timestamp=" + timestamp;
    String signature = this.utilService.generateSignature(apiEndPoint, "", apiSecret);
    String url = apiUrl + apiEndPoint;
    String authHeader = apiVersion.toUpperCase() + "-HMAC-SHA512 Key=" + apiKey + ",Signature=" + signature;
    headers.set("Authorization", authHeader);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(null, headers);
    return this.restTemplate.exchange(url, HttpMethod.GET, request, TransactionResponse.class);
  }
}
