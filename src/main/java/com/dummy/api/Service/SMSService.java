package com.dummy.api.Service;

import com.dummy.api.SMSsender;
import com.dummy.api.SmsRequest;
import com.dummy.api.TwilioConfiguration;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("twilio")
public class SMSService implements SMSsender {

    private final TwilioConfiguration twilioConfiguration;

    @Autowired
    public SMSService(TwilioConfiguration twilioConfiguration) {
        this.twilioConfiguration = twilioConfiguration;
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        return true;
    }

    @Override
    public void sendSMS(SmsRequest smsRequest) {
        if (isPhoneNumberValid(smsRequest.getPhoneNumber())) {
            PhoneNumber to = new PhoneNumber(smsRequest.getPhoneNumber());
            PhoneNumber from = new PhoneNumber("+18053035074");
            String message = smsRequest.getMessage();
            MessageCreator creator = Message.creator(to, from, message);
            creator.create();
        } else {
            throw new IllegalArgumentException(
                    "Phone Number[" + smsRequest.getPhoneNumber() + "] is not a valid number"
            );
        }

    }

}