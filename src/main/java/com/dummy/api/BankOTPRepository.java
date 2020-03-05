package com.dummy.api;

import com.dummy.api.models.BankOTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankOTPRepository extends JpaRepository<BankOTP, Long> {
    Optional<BankOTP> findByToken(int token);

    BankOTP findByBankCard(BankOTP bankOTP);
}
