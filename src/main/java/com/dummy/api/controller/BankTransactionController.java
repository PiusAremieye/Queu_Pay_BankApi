package com.dummy.api.controller;

import com.dummy.api.Service.BankTransactionsService;
import com.dummy.api.Service.MapValidationErrorService;
import com.dummy.api.apiresponse.ApiResponse;
import com.dummy.api.dto.TransferDto;
import com.dummy.api.models.BankTransactionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class BankTransactionController {
  private BankTransactionsService bankTransactionsService;
  private MapValidationErrorService mapValidationErrorService;

  @Autowired
  public BankTransactionController(BankTransactionsService bankTransactionsService, MapValidationErrorService mapValidationErrorService) {
    this.bankTransactionsService = bankTransactionsService;
    this.mapValidationErrorService = mapValidationErrorService;
  }

  @GetMapping("/{accountNumber}")
  public ResponseEntity<ApiResponse<List<BankTransactionDetails>>> getTransactionDetailsByPin(@PathVariable(value = "accountNumber") Long accountNumber, HttpServletRequest request) {
    List<BankTransactionDetails> transactionDetails = bankTransactionsService.getTransactionDetailsForCardUser(accountNumber, request);
    ApiResponse<List<BankTransactionDetails>> response = new ApiResponse<>(HttpStatus.OK);
    response.setData(transactionDetails);
    response.setMessage("All transactions retrieved successful!");
    return new ResponseEntity<>(response, response.getStatus());
  }

  @PostMapping("/debit/{token}")
  public ResponseEntity<?> transferToAnotherAccount(@PathVariable(value = "token") Integer token, @Valid @RequestBody TransferDto transferDto, HttpServletRequest request, BindingResult bindingResult) {
    ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(bindingResult);
    if (errorMap != null){
      return errorMap;
    }
    BankTransactionDetails transactionDetails = bankTransactionsService.debitBankWithTransfer(transferDto, token, request);
    ApiResponse<BankTransactionDetails> response = new ApiResponse<>(HttpStatus.OK);
    response.setData(transactionDetails);
    response.setMessage("Transfer successfully!");
    return new ResponseEntity<>(response, response.getStatus());
  }


//  @PutMapping("/transaction/credits/{bankCardNumber}/{amount}")
//  public BankTransactionDetails updateBankTransactionDetails(@Valid @RequestBody BankTransactionDetails bankTransactionDetails, @PathVariable("bankCardNumber") long bankCardNumber,
//                                                             @PathVariable("amount") Long amount) throws ResourceNotFoundException {
//    return getBankTransactionDetails(bankTransactionDetails, bankCardNumber, amount);
//  }
//
//  private BankTransactionDetails getBankTransactionDetails(BankTransactionDetails bankTransactionDetails, Long bankCardNumber, Long amount) throws ResourceNotFoundException {
//    System.out.println(bankCardRepository.findByBankCardNumber(bankCardNumber));
//    Optional<BankCard> bankCard = bankCardRepository.findByBankCardNumber(bankCardNumber);
//    return getBankTransactionDetails(bankTransactionDetails, amount, bankCard);
//  }
//
//  private BankTransactionDetails getBankTransactionDetails(BankTransactionDetails bankTransactionDetails, Long amount, Optional<BankCard> bankCard) throws ResourceNotFoundException {
//    if (bankCard.isEmpty()) {
//      throw new ResourceNotFoundException("Card details not found");
//    }
//    BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankCard.get());
//    bankTransactionDetails1.setAmount(amount);
//    bankTransactionDetails1.setCredit(bankTransactionDetails.getAmount()+bankTransactionDetails1.getCredit());
//    bankTransactionDetails1.setTotalCredit(bankTransactionDetails.getTotalCredit() + bankTransactionDetails1.getCredit());
//    bankTransactionDetails1.setAccountBalance(bankTransactionDetails.getAccountBalance() + bankTransactionDetails1.getCredit());
//    bankTransactionDetails1 = bankTransactionDetailsRepository.save(bankTransactionDetails1);
//    System.out.println(bankTransactionDetails1);
//    return bankTransactionDetails1;
//  }

//  private BankTransactionDetails getBankTransactionDetails(BankTransactionDetails bankTransactionDetails, Integer pin, Long amount) throws ResourceNotFoundException {
//    System.out.println(bankCardRepository.findByPin(pin));
//    Optional<BankCard> bankCard = bankCardRepository.findByPin(pin);
//    return getBankTransactionDetails(bankTransactionDetails, amount, bankCard);
//  }

//  @PutMapping("/transaction/debit/{token}/{amount}")
//  public BankTransactionDetails debitBankTransactionDetails(@PathVariable("token") Integer token, @PathVariable("amount") Long amount) throws ResourceNotFoundException {
//    Optional<BankOTP> bankOTP = bankOTPRepository.findByToken(token);
//    if (bankOTP.isEmpty()) {
//      throw new ResourceNotFoundException("OTP no longer exists!!!");
//    }
//    BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankOTP.get().getBankCard());
//    bankTransactionDetails1.setAmount(amount);
//    if(bankTransactionDetails1.getAccountBalance()-bankTransactionDetails1.getAmount() <= 0){
//      throw new ResourceNotFoundException("Insufficient Balance!!!");
//    }
//    bankTransactionDetails1.setDebit(bankTransactionDetails1.getAmount()+amount);
//    bankTransactionDetails1.setTotalDebit(bankTransactionDetails1.getTotalDebit() + bankTransactionDetails1.getDebit());
//    bankTransactionDetails1.setAccountBalance(bankTransactionDetails1.getAccountBalance() - bankTransactionDetails1.getDebit());
//    return bankTransactionDetailsRepository.save(bankTransactionDetails1);
//  }
//
//
//  @PutMapping("/transaction/credit/{token}/{amount}")
//  public BankTransactionDetails updateCreditBankTransactionDetails(@PathVariable(value = "token") int token, @PathVariable(value = "amount") Long amount) throws ResourceNotFoundException {
//
//    Optional<BankOTP> bankOTP = bankOTPRepository.findByToken(token);
//    if (bankOTP.isEmpty()) {
//      throw new ResourceNotFoundException("OTP no longer exists!!!");
//    }
//    BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankOTP.get().getBankCard());
//    bankTransactionDetails1.setAmount(amount);
//    bankTransactionDetails1.setCredit(bankTransactionDetails1.getAmount()+bankTransactionDetails1.getCredit());
//    bankTransactionDetails1.setTotalCredit(bankTransactionDetails1.getTotalCredit() + bankTransactionDetails1.getCredit());
//    bankTransactionDetails1.setAccountBalance(bankTransactionDetails1.getAccountBalance() + bankTransactionDetails1.getCredit());
//    return bankTransactionDetailsRepository.save(bankTransactionDetails1);
//  }
}
