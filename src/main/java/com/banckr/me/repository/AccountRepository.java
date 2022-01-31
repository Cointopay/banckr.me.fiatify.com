package com.banckr.me.repository;

import com.banckr.me.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

  @Query(value="SELECT * FROM users WHERE email = ? AND password = ? LIMIT 1", nativeQuery = true)
  Optional<Account> findAccount(String email, String password);

  @Query(value="SELECT * FROM users WHERE profile_id = ? LIMIT 1", nativeQuery = true)
  Optional<Account> findAccountByProfileId(String profileId);
}
