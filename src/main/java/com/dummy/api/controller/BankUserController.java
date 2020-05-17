package com.dummy.api.controller;


import com.dummy.api.Service.BankUserService;
import com.dummy.api.Service.MapValidationErrorService;
import com.dummy.api.apiresponse.ApiResponse;
import com.dummy.api.dto.SignUpDto;
import com.dummy.api.models.BankUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class BankUserController {

  private BankUserService bankUserService;
  private MapValidationErrorService mapValidationErrorService;

  @Autowired
  public BankUserController(BankUserService bankUserService, MapValidationErrorService mapValidationErrorService) {
    this.bankUserService = bankUserService;
    this.mapValidationErrorService = mapValidationErrorService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> signUp(@RequestBody @Valid SignUpDto signUpDto, BindingResult bindingResult) {
    ResponseEntity<?> errorMap = mapValidationErrorService.MapValidationService(bindingResult);
    if (errorMap != null){
      return errorMap;
    }
    BankUser createdUser = bankUserService.createNewBankDetailsForUser(signUpDto);
    ApiResponse<BankUser> response = new ApiResponse<>(HttpStatus.CREATED);
    response.setData(createdUser);
    response.setMessage("Registration successful!");
    return new ResponseEntity<>(response, response.getStatus());
  }

}
