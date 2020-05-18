package com.dummy.api.controller;

import com.dummy.api.Service.BankCardService;
import com.dummy.api.Service.MapValidationErrorService;
import com.dummy.api.apiresponse.ApiResponse;
import com.dummy.api.dto.CardDto;
import com.dummy.api.models.BankCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bankcard")
public class BankCardController {
  private BankCardService bankCardService;
  private MapValidationErrorService mapValidationErrorService;

  @Autowired
  public BankCardController(BankCardService bankCardService, MapValidationErrorService mapValidationErrorService) {
    this.bankCardService = bankCardService;
    this.mapValidationErrorService = mapValidationErrorService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<BankCard>>> getAllBankCardDetails() {
    List<BankCard> allBankCards = bankCardService.getAllBankCardDetails();
    ApiResponse<List<BankCard>> response = new ApiResponse<>(HttpStatus.OK);
    response.setData(allBankCards);
    response.setMessage("All bank cards retrieved successful!");
    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/{accountNumber}")
  public ResponseEntity<ApiResponse<BankCard>> getBankCardDetailsByBankAccountNumber(@PathVariable(value = "accountNumber") Long accountNumber, HttpServletRequest request) {
    BankCard bankCard = bankCardService.getBankCardDetailsByBankAccountNumber(accountNumber, request);
    ApiResponse<BankCard> response = new ApiResponse<>(HttpStatus.OK);
    response.setData(bankCard);
    response.setMessage("BandCard retrieved successfully!");
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

}
