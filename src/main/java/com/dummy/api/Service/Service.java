package com.dummy.api.Service;


import com.dummy.api.SmsRequest;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class Service {
    private final SMSService smsService;

    @Autowired
    public Service(SMSService smsService) {
        this.smsService = smsService;
    }

    public void sendSms(SmsRequest smsRequest) {
        smsService.sendSMS(smsRequest);
    }
}
