package com.dummy.api.Service.Implementation;

import com.dummy.api.Service.BankCardService;
import com.dummy.api.Service.BankTransactionsService;
import com.dummy.api.dto.TransferDto;
import com.dummy.api.exception.CustomException;
import com.dummy.api.models.BankCard;
import com.dummy.api.models.BankOTP;
import com.dummy.api.models.BankTransactionDetails;
import com.dummy.api.models.BankUser;
import com.dummy.api.repository.BankOTPRepository;
import com.dummy.api.repository.BankTransactionDetailsRepository;
import com.dummy.api.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class BankTransactionServiceImpl implements BankTransactionsService {
  private BankTransactionDetailsRepository bankTransactionDetailsRepository;
  private BankOTPRepository bankOTPRepository;
  private BankCardService bankCardService;
  private JwtProvider jwtProvider;

  @Autowired
  public BankTransactionServiceImpl(BankTransactionDetailsRepository bankTransactionDetailsRepository, BankOTPRepository bankOTPRepository, BankCardService bankCardService, JwtProvider jwtProvider) {
    this.bankTransactionDetailsRepository = bankTransactionDetailsRepository;
    this.bankOTPRepository = bankOTPRepository;
    this.bankCardService = bankCardService;
    this.jwtProvider = jwtProvider;
  }

  @Override
  @Transactional
  public Double getAccountBalance(Long accountNumber, HttpServletRequest request) {
    double totalCredit = 0.0;
    double totalDebit = 0.0;
    double accountBalance;
    List<BankTransactionDetails> totalTransactions = getTransactionDetailsForCardUser(accountNumber, request);
    for (BankTransactionDetails transaction : totalTransactions){
      totalCredit += transaction.getCredit();
      totalDebit += transaction.getDebit();
    }
    accountBalance = totalCredit - totalDebit;
    return accountBalance;
  }

  @Override
  public List<BankTransactionDetails> getTransactionDetailsForCardUser(Long accountNumber, HttpServletRequest request) {
    BankCard bankCard = bankCardService.getBankCardDetailsByBankAccountNumber(accountNumber, request);
    return bankTransactionDetailsRepository.findByBankCard(bankCard);
  }

  @Override
  public BankTransactionDetails debitBankWithTransfer(TransferDto transferDto, Integer token, HttpServletRequest request){
    BankUser bankUser = jwtProvider.resolveUser(request);
    Optional<BankOTP> bankOTP = bankOTPRepository.findByToken(token);
    if (bankOTP.isEmpty()) {
      throw new CustomException("OTP does not exists!!!", HttpStatus.NOT_FOUND);
    }

    Long accountNumber = transferDto.getAccountNumber();
    BankCard bankCard = bankCardService.findAnyBankAccount(accountNumber);
    Double accountBalance = getAccountBalance(accountNumber, request);

    if (bankCard.getBankUser().equals(bankUser)){
      throw new CustomException("Cannot transfer to your account", HttpStatus.BAD_REQUEST);
    }

    if(transferDto.getAmount() >= accountBalance){
      throw new CustomException("Insufficient Balance!!!", HttpStatus.BAD_REQUEST);
    }
    BankTransactionDetails bankTransactionDetails = new BankTransactionDetails();
    bankTransactionDetails.setAmount(transferDto.getAmount());
    bankTransactionDetails.setDebit(transferDto.getAmount());
    bankTransactionDetails.setBankCard(bankCard);
    bankTransactionDetails.setAccountBalance(accountBalance - transferDto.getAmount());
    return bankTransactionDetailsRepository.save(bankTransactionDetails);
  }

}
