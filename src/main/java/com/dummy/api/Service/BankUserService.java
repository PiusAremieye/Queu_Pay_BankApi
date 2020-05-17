package com.dummy.api.Service;

import com.dummy.api.dto.SignUpDto;
import com.dummy.api.models.BankUser;

public interface BankUserService {
  BankUser createNewBankDetailsForUser(SignUpDto signUpDto);
}
