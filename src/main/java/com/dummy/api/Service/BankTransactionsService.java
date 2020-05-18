package com.dummy.api.Service;

import com.dummy.api.dto.TransferDto;
import com.dummy.api.models.BankTransactionDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BankTransactionsService {
  Double getAccountBalance(Long accountNumber, HttpServletRequest request);
  List<BankTransactionDetails> getTransactionDetailsForCardUser(Long accountNumber, HttpServletRequest request);
  BankTransactionDetails debitBankWithTransfer(TransferDto transferDto, Integer token, HttpServletRequest request);
}
