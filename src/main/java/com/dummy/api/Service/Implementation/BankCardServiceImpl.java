package com.dummy.api.Service.Implementation;

import com.dummy.api.Service.BankCardService;
import com.dummy.api.dto.CardDto;
import com.dummy.api.exception.CustomException;
import com.dummy.api.models.BankCard;
import com.dummy.api.models.BankUser;
import com.dummy.api.repository.BankCardRepository;
import com.dummy.api.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class BankCardServiceImpl implements BankCardService {
  private BankCardRepository bankCardRepository;
  private JwtProvider jwtProvider;

  @Autowired
  public BankCardServiceImpl(BankCardRepository bankCardRepository, JwtProvider jwtProvider) {
    this.bankCardRepository = bankCardRepository;
    this.jwtProvider = jwtProvider;
  }

  @Override
  public BankCard createNewBankCardDetailsForUser(CardDto cardDto, HttpServletRequest request) {
    BankUser bankUser = jwtProvider.resolveUser(request);
    BankCard bankCard = new BankCard();
    bankCard.setBankCardNumber(cardDto.getBankCardNumber());
    bankCard.setBankCardType(cardDto.getBankCardType());
    bankCard.setCvv(cardDto.getCvv());
    bankCard.setMonthOfExpiry(cardDto.getMonthOfExpiry());
    bankCard.setPin(cardDto.getPin());
    bankCard.setYearOfExpiry(cardDto.getYearOfExpiry());
    bankCard.setBankUser(bankUser);
    bankCardRepository.save(bankCard);
    return bankCard;
  }

  @Override
  public List<BankCard> getAllBankCardDetails() {
    return bankCardRepository.findAll();
  }

  @Override
  public BankCard getBankCardDetailsByBankAccountNumber(Long bankAccountNumber) {
    return bankCardRepository.findById(bankAccountNumber).orElseThrow(() -> new CustomException("No such Account Number exists!!!", HttpStatus.NOT_FOUND));
  }
}
