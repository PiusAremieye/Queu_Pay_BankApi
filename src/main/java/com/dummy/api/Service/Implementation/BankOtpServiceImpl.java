package com.dummy.api.Service.Implementation;

import com.dummy.api.Service.BankCardService;
import com.dummy.api.Service.BankOtpService;
import com.dummy.api.dto.SmsRequest;
import com.dummy.api.models.BankCard;
import com.dummy.api.models.BankOTP;
import com.dummy.api.repository.BankOTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@Service
public class BankOtpServiceImpl implements BankOtpService {
  private BankOTPRepository bankOTPRepository;
  private BankCardService bankCardService;
  private SMSServiceImpl smsService;

  @Autowired
  public BankOtpServiceImpl(BankOTPRepository bankOTPRepository, BankCardService bankCardService, SMSServiceImpl smsService) {
    this.bankOTPRepository = bankOTPRepository;
    this.bankCardService = bankCardService;
    this.smsService = smsService;
  }

  public Integer setAndSendOTPtoUser(Long accountNumber, HttpServletRequest request){
    BankCard bankCard = bankCardService.getBankCardDetailsByBankAccountNumber(accountNumber, request);
    Random rnd = new Random(5);
    int tokenNumber = rnd.nextInt(999999);

    String phoneNumber = bankCard.getBankUser().getPhoneNumber();
    BankOTP bankOTP = new BankOTP();
    bankOTP.setBankCard(bankCard);
    bankOTP.setToken(tokenNumber);
    bankOTPRepository.save(bankOTP);
    SmsRequest smsRequest = new SmsRequest(phoneNumber, "Your One-Time Password is "+tokenNumber);
    smsService.sendSMS(smsRequest);
    return tokenNumber;
  }

  @Scheduled(fixedDelay=300000)
  public void removeOTP() {
    bankOTPRepository.deleteAll();
  }
}
