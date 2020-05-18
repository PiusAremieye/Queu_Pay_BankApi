package com.dummy.api.Service;

import javax.servlet.http.HttpServletRequest;

public interface BankOtpService {
  Integer setAndSendOTPtoUser(Long accountNumber, HttpServletRequest request);
}
