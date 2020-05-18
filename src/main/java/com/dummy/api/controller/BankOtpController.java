package com.dummy.api.controller;

import com.dummy.api.Service.BankOtpService;
import com.dummy.api.apiresponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class BankOtpController {
  private BankOtpService bankOtpService;

  @Autowired
  public BankOtpController(BankOtpService bankOtpService) {
    this.bankOtpService = bankOtpService;
  }

  @PostMapping("/transaction/{accountNumber}/otp")
  public ResponseEntity<ApiResponse<Integer>> sendOtp(@PathVariable(value = "accountNumber") Long accountNumber, HttpServletRequest request) {
    Integer otp = bankOtpService.setAndSendOTPtoUser(accountNumber, request);
    ApiResponse<Integer> response = new ApiResponse<>(HttpStatus.OK);
    response.setData(otp);
    response.setMessage("Otp has been sent");
    return new ResponseEntity<>(response, response.getStatus());
  }
}
