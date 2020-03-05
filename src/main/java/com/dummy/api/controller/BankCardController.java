package com.dummy.api.controller;

import com.dummy.api.*;
import com.dummy.api.Service.Service;
import com.dummy.api.models.BankCard;
import com.dummy.api.models.BankOTP;
import com.dummy.api.models.BankTransactionDetails;
import com.dummy.api.models.BankUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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

    @PutMapping("/transaction/credits/{pin}/{amount}")
    public BankTransactionDetails updateBankTransactionDetails(@Valid @RequestBody BankTransactionDetails bankTransactionDetails, @PathVariable Integer pin, Integer amount) throws ResourceNotFoundException {
        return getBankTransactionDetails(bankTransactionDetails,pin, amount);
    }

    private BankTransactionDetails getBankTransactionDetails(@Valid @RequestBody BankTransactionDetails bankTransactionDetails, @PathVariable Integer pin, Integer amount) throws ResourceNotFoundException {
        return bankCardRepository.findByPin(pin).map(bankcard ->{
            BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankcard);
            bankTransactionDetails1.setCredit(bankTransactionDetails.getAmount()+bankTransactionDetails1.getCredit());
            bankTransactionDetails1.setTotalCredit(bankTransactionDetails.getTotalCredit() + bankTransactionDetails1.getCredit());
            bankTransactionDetails1.setAccountBalance(bankTransactionDetails.getAccountBalance() + bankTransactionDetails1.getCredit());
            bankTransactionDetails1.setAmount(bankTransactionDetails.getAmount());
            return bankTransactionDetailsRepository.save(bankTransactionDetails1);
        }).orElseThrow(() -> new ResourceNotFoundException("This account details is not available on our server!!!"));
    }

    @PutMapping("/transaction/debit/{token}/{amount}")
    public BankTransactionDetails updateBankTransactionDetails(@Valid @RequestBody BankTransactionDetails bankTransactionDetails, @PathVariable int token, Long amount) throws ResourceNotFoundException {
        return bankOTPRepository.findByToken(token).map(bankOTP ->{
            final BankTransactionDetails bankTransactionDetails1 = bankTransactionDetailsRepository.findByBankCard(bankOTP.getBankCard());
            bankTransactionDetails1.setDebit(bankTransactionDetails.getDebit());
            bankTransactionDetails1.setTotalDebit(bankTransactionDetails.getTotalDebit() + bankTransactionDetails.getDebit());
            bankTransactionDetails1.setAccountBalance(bankTransactionDetails.getAccountBalance() - bankTransactionDetails.getDebit());
            bankTransactionDetails1.setAmount(bankTransactionDetails.getAmount());
            return bankTransactionDetailsRepository.save(bankTransactionDetails1);
        }).orElseThrow(() -> new ResourceNotFoundException("Insufficient Balance!!!"));
    }

    @PostMapping("/transaction/debit/{pin}")
    public List<BankOTP> setAndSendOTPtoUser(@PathVariable(value = "pin") int pin, @Valid @RequestBody BankOTP bankOtp) throws ResourceNotFoundException{

        Random rnd = new Random();
        int tokenNumber = rnd.nextInt(999999);
        SmsRequest smsRequest = new SmsRequest("+2348154794351", "Your One-Time Password is "+tokenNumber);
        service.sendSms(smsRequest);
        return bankCardRepository.findByPin(pin).map(bankcard ->{
            bankOtp.setBankCard(bankcard);
            bankOtp.setToken(tokenNumber);
            bankOTPRepository.save(bankOtp);
        return bankOTPRepository.findAll();
        }).orElseThrow(() ->new ResourceNotFoundException("No such account number was found!!!"));
    }
    @Scheduled(fixedDelay=300000)
    public void removeOTP() {
        bankOTPRepository.deleteAll();
    }


}
