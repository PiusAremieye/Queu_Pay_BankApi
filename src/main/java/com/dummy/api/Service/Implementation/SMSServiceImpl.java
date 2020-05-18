package com.dummy.api.Service.Implementation;

import com.dummy.api.Service.SMSsender;
import com.dummy.api.dto.SmsRequest;
import com.dummy.api.configuration.TwilioConfiguration;
import com.dummy.api.exception.CustomException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service("twilio")
public class SMSServiceImpl implements SMSsender {

  private final TwilioConfiguration twilioConfiguration;

  @Autowired
  public SMSServiceImpl(TwilioConfiguration twilioConfiguration) {
    this.twilioConfiguration = twilioConfiguration;
  }

  private boolean isPhoneNumberValid(String phoneNumber) {
    return true;
  }

  @Override
  public void sendSMS(SmsRequest smsRequest) {
    if (isPhoneNumberValid(smsRequest.getPhoneNumber())) {
      PhoneNumber to = new PhoneNumber(smsRequest.getPhoneNumber());
      PhoneNumber from = new PhoneNumber(twilioConfiguration.getTrialNumber());
      String message = smsRequest.getMessage();
      MessageCreator creator = Message.creator(to, from, message);
      creator.create();
    } else {
      throw new CustomException(
        "Phone Number[" + smsRequest.getPhoneNumber() + "] is not a valid number", HttpStatus.BAD_REQUEST
      );
    }

  }

}