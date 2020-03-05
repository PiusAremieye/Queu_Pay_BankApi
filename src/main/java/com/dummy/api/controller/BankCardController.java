package com.dummy.api.controller;

import com.dummy.api.*;
import com.dummy.api.Service.Service;
import com.dummy.api.models.BankCard;
import com.dummy.api.models.BankOTP;
import com.dummy.api.models.BankTransactionDetails;
import com.dummy.api.models.BankUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/v1")
public class BankCardController {

    @Autowired
    private BankCardRepository bankCardRepository;

    @Autowired
    private BankUserRepository bankUserRepository;

    @Autowired
    private BankTransactionDetailsRepository bankTransactionDetailsRepository;

    @Autowired
    private BankOTPRepository bankOTPRepository;

   private final Service service;

    @Autowired
    private SimpMessagingTemplate webSocket;

    private final String  TOPIC_DESTINATION = "/topic/sms";

    public BankCardController(Service service) {
        this.service = service;
    }

    public void sendSMS(SmsRequest smsRequest) {
        service.sendSms(smsRequest);
    }

    @GetMapping("/bankcard")
    public List<BankCard> getAllBankCarddetails() {
        return bankCardRepository.findAll();
    }



    @GetMapping("/bankcard/{bankaccountnumber}")
    public ResponseEntity<BankCard> getBankCardDetailsByBankAccountNumber(@PathVariable Long bankAcountNumber) throws ResourceNotFoundException {

        BankCard bankCard = bankCardRepository.findById(bankAcountNumber).orElseThrow(() -> new ResourceNotFoundException("No such Account Number exists!!!"));
        return ResponseEntity.ok().body(bankCard);
    }

    @PostMapping("/bankcard/{id}")
    public BankCard createNewBankCardDetailsForUser(@Valid @RequestBody BankCard bankCard, @PathVariable Long id) throws ResourceNotFoundException {

        return bankUserRepository.findById(id).map(bankuser -> {
            bankCard.setBankUser(bankuser);
            return bankCardRepository.save(bankCard);
        }).orElseThrow(() -> new ResourceNotFoundException("This Account user was not found!!!"));
    }



    @PostMapping("/user")
    public List<BankUser> createNewBankDetailsForUser(@Valid @RequestBody BankUser bankUser){
        bankUserRepository.save(bankUser);
        return  bankUserRepository.findAll();
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
