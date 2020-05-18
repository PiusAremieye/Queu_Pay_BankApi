package com.dummy.api.Service;

import com.dummy.api.dto.CardDto;
import com.dummy.api.models.BankCard;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BankCardService {
  BankCard createNewBankCardDetailsForUser(CardDto cardDto, HttpServletRequest request);
  List<BankCard> getAllBankCardDetails();
  BankCard getBankCardDetailsByBankAccountNumber(Long bankAccountNumber, HttpServletRequest request);
  BankCard findAnyBankAccount(Long bankAccountNumber);
}
