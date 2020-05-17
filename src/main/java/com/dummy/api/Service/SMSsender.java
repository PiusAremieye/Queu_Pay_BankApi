package com.dummy.api.Service;

import com.dummy.api.dto.SmsRequest;

public interface SMSsender {
  void sendSMS(SmsRequest smsRequest);
}
