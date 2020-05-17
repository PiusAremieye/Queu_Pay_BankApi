package com.dummy.api.controller;

import com.dummy.api.Service.BankCardService;
import com.dummy.api.Service.MapValidationErrorService;
import com.dummy.api.Service.SMSsender;
import com.dummy.api.apiresponse.ApiResponse;
import com.dummy.api.dto.CardDto;
import com.dummy.api.dto.SignUpDto;
import com.dummy.api.dto.SmsRequest;
import com.dummy.api.models.BankCard;
import com.dummy.api.models.BankOTP;
import com.dummy.api.models.BankTransactionDetails;
import com.dummy.api.models.BankUser;
import com.dummy.api.repository.BankCardRepository;
import com.dummy.api.repository.BankOTPRepository;
import com.dummy.api.repository.BankTransactionDetailsRepository;
import com.dummy.api.repository.BankUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/bankcard")
public class BankCardController {
  private BankCardService bankCardService;
  private BankTransactionDetailsRepository bankTransactionDetailsRepository;
  private BankOTPRepository bankOTPRepository;
  private MapValidationErrorService mapValidationErrorService;
  private SMSsender smsSender;
  private SimpMessagingTemplate webSocket;
  private final String  TOPIC_DESTINATION = "/topic/sms";

  @Autowired
  public BankCardController(BankCardService bankCardService, BankTransactionDetailsRepository bankTransactionDetailsRepository, BankOTPRepository bankOTPRepository, MapValidationErrorService mapValidationErrorService, SMSsender smsSender, SimpMessagingTemplate webSocket) {
    this.bankCardService = bankCardService;
    this.bankTransactionDetailsRepository = bankTransactionDetailsRepository;
    this.bankOTPRepository = bankOTPRepository;
    this.mapValidationErrorService = mapValidationErrorService;
    this.smsSender = smsSender;
    this.webSocket = webSocket;
  }

  public void sendSMS(SmsRequest smsRequest) {
      service.sendSms(smsRequest);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<BankCard>>> getAllBankCardDetails() {
    List<BankCard> allBankCards = bankCardService.getAllBankCardDetails();
    ApiResponse<List<BankCard>> response = new ApiResponse<>(HttpStatus.OK);
    response.setData(allBankCards);
    response.setMessage("All bank cards retrieved successful!");
    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/bankcard/{bankaccountnumber}")
  public ResponseEntity<ApiResponse<BankCard>> getBankCardDetailsByBankAccountNumber(@PathVariable(value = "bankaccountnumber") Long bankaccountnumber) {
    BankCard bankCard = bankCardService.getBankCardDetailsByBankAccountNumber(bankaccountnumber);
    ApiResponse<BankCard> response = new ApiResponse<>(HttpStatus.OK);
    response.setData(bankCard);
    response.setMessage("All bank cards retrieved successful!");
    return new ResponseEntity<>(response, response.getStatus());
  }

  @PostMapping()
  public ResponseEntity<?> createNewBankCardDetailsForUser(@Valid @RequestBody CardDto cardDto, BindingResult bindingResult, HttpServletRequest request){
    ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(bindingResult);
    if (errorMap != null){
      return errorMap;
    }
    BankCard bankCard = bankCardService.createNewBankCardDetailsForUser(cardDto, request);
    ApiResponse<BankCard> response = new ApiResponse<>(HttpStatus.CREATED);
    response.setData(bankCard);
    response.setMessage("Bank card registered successfully!");
    return new ResponseEntity<>(response, response.getStatus());
  }


//    @PutMapping("/transaction/credit/{pin}/{amount}")
//    public BankTransactionDetails createNewBankTransactionDetailsForBankCard(@Valid @RequestBody BankTransactionDetails bankTransactionDetails, @PathVariable Integer pin, Integer amount) throws ResourceNotFoundException {
//        return getBankTransactionDetails(bankTransactionDetails, pin, amount);
//    }

  @GetMapping("/transaction/{pin}")
  public List<BankTransactionDetails> getTransactionDetailsForCardUser(@PathVariable Integer pin) {
      bankCardRepository.findByPin(pin);
      return bankTransactionDetailsRepository.findAll();
  }

  @PostMapping("/transaction/transfer-credit/{pin}/{amount}")
  public BankTransactionDetails createNewBankTransactionDetails(@Valid @RequestBody BankTransactionDetails bankTransactionDetails, @PathVariable Integer pin, Integer amount) throws ResourceNotFoundException {
      return bankTransactionDetailsRepository.save(bankTransactionDetails);
  }

  @PutMapping("/transaction/credits/{bankCardNumber}/{amount}")
  public BankTransactionDetails updateBankTransactionDetails(@Valid @RequestBody BankTransactionDetails bankTransactionDetails, @PathVariable("bankCardNumber") long bankCardNumber,
                                                             @PathVariable("amount") Long amount) throws ResourceNotFoundException {
      return getBankTransactionDetails(bankTransactionDetails, bankCardNumber, amount);
  }

  private BankTransactionDetails getBankTransactionDetails(BankTransactionDetails bankTransactionDetails, Long bankCardNumber, Long amount) throws ResourceNotFoundException {
      System.out.println(bankCardRepository.findByBankCardNumber(bankCardNumber));
      Optional<BankCard> bankCard = bankCardRepository.findByBankCardNumber(bankCardNumber);
      return getBankTransactionDetails(bankTransactionDetails, amount, bankCard);
  }

  private BankTransactionDetails getBankTransactionDetails(BankTransactionDetails bankTransactionDetails, Long amount, Optional<BankCard> bankCard) throws ResourceNotFoundException {
      if (bankCard.isEmpty()) {
          throw new ResourceNotFoundException("Card details not found");
      }
      BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankCard.get());
      bankTransactionDetails1.setAmount(amount);
      bankTransactionDetails1.setCredit(bankTransactionDetails.getAmount()+bankTransactionDetails1.getCredit());
      bankTransactionDetails1.setTotalCredit(bankTransactionDetails.getTotalCredit() + bankTransactionDetails1.getCredit());
      bankTransactionDetails1.setAccountBalance(bankTransactionDetails.getAccountBalance() + bankTransactionDetails1.getCredit());
      bankTransactionDetails1 = bankTransactionDetailsRepository.save(bankTransactionDetails1);
      System.out.println(bankTransactionDetails1);
      return bankTransactionDetails1;
  }

  private BankTransactionDetails getBankTransactionDetails(BankTransactionDetails bankTransactionDetails, Integer pin, Long amount) throws ResourceNotFoundException {
      System.out.println(bankCardRepository.findByPin(pin));
      Optional<BankCard> bankCard = bankCardRepository.findByPin(pin);
      return getBankTransactionDetails(bankTransactionDetails, amount, bankCard);
  }

  @PutMapping("/transaction/debit/{token}/{amount}")
  public BankTransactionDetails debitBankTransactionDetails(@PathVariable("token") Integer token, @PathVariable("amount") Long amount) throws ResourceNotFoundException {
      Optional<BankOTP> bankOTP = bankOTPRepository.findByToken(token);
      if (bankOTP.isEmpty()) {
          throw new ResourceNotFoundException("OTP no longer exists!!!");
      }
      BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankOTP.get().getBankCard());
      bankTransactionDetails1.setAmount(amount);
      if(bankTransactionDetails1.getAccountBalance()-bankTransactionDetails1.getAmount() <= 0){
          throw new ResourceNotFoundException("Insufficient Balance!!!");
      }
      bankTransactionDetails1.setDebit(bankTransactionDetails1.getAmount()+amount);
      bankTransactionDetails1.setTotalDebit(bankTransactionDetails1.getTotalDebit() + bankTransactionDetails1.getDebit());
      bankTransactionDetails1.setAccountBalance(bankTransactionDetails1.getAccountBalance() - bankTransactionDetails1.getDebit());
      return bankTransactionDetailsRepository.save(bankTransactionDetails1);
      }

  @PutMapping("/transaction/transfer-debit/{bankaccountnumber}/{amount}")
  public BankTransactionDetails debitBankTransactionWithBankAccountNumber( @PathVariable("bankaccountnumber") Long bankaccountnumber, @PathVariable("amount") Long amount) throws ResourceNotFoundException {

      Optional<BankCard> bankCard = bankCardRepository.findByBankAccountNumber(bankaccountnumber);
      if (bankCard.isEmpty()) {
          throw new ResourceNotFoundException("This Account holder doesn't exists!!!");
      }
      BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankCard);
      bankTransactionDetails1.setAmount(amount);
      if(bankTransactionDetails1.getAccountBalance()-amount<= 0){
          throw new ResourceNotFoundException("Insufficient Balance!!!");
      }
      bankTransactionDetails1.setDebit(bankTransactionDetails1.getAmount()+amount);
      bankTransactionDetails1.setTotalDebit(bankTransactionDetails1.getTotalDebit() + bankTransactionDetails1.getDebit());
      bankTransactionDetails1.setAccountBalance(bankTransactionDetails1.getAccountBalance() - amount);
      return bankTransactionDetailsRepository.save(bankTransactionDetails1);
  }

  @PutMapping("/transaction/credit/{token}/{amount}")
  public BankTransactionDetails updateCreditBankTransactionDetails(@PathVariable(value = "token") int token, @PathVariable(value = "amount") Long amount) throws ResourceNotFoundException {

      Optional<BankOTP> bankOTP = bankOTPRepository.findByToken(token);
      if (bankOTP.isEmpty()) {
      throw new ResourceNotFoundException("OTP no longer exists!!!");
  }
  BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankOTP.get().getBankCard());
      bankTransactionDetails1.setAmount(amount);
      bankTransactionDetails1.setCredit(bankTransactionDetails1.getAmount()+bankTransactionDetails1.getCredit());
      bankTransactionDetails1.setTotalCredit(bankTransactionDetails1.getTotalCredit() + bankTransactionDetails1.getCredit());
      bankTransactionDetails1.setAccountBalance(bankTransactionDetails1.getAccountBalance() + bankTransactionDetails1.getCredit());
      return bankTransactionDetailsRepository.save(bankTransactionDetails1);
}




  @PostMapping("/transaction/debit/{pin}")
  public List<BankOTP> setAndSendOTPtoUser(@PathVariable(value = "pin") int pin, @Valid @RequestBody BankOTP bankOtp) throws ResourceNotFoundException{
      Random rnd = new Random();
      int tokenNumber = rnd.nextInt(999999);
      return bankCardRepository.findByPin(pin).map(bankcard ->{
          String phoneNumber = bankcard.getBankUser().getPhoneNumber();
          bankOtp.setBankCard(bankcard);
          bankOtp.setToken(tokenNumber);
          bankOTPRepository.save(bankOtp);
          SmsRequest smsRequest = new SmsRequest(phoneNumber, "Your One-Time Password is "+tokenNumber);
          service.sendSms(smsRequest);
      return bankOTPRepository.findAll();
      }).orElseThrow(() ->new ResourceNotFoundException("No such account number was found!!!"));
  }
  @Scheduled(fixedDelay=300000)
  public void removeOTP() {
      bankOTPRepository.deleteAll();
  }


}
