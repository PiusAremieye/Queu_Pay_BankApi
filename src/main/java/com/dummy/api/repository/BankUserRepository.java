package com.dummy.api.repository;

import com.dummy.api.models.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankUserRepository extends JpaRepository<BankUser, Long> {
  Optional<BankUser> findByUsername(String username);
  boolean existsByUsername(String username);
}
