package com.dummy.api.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class BankCard implements Serializable {

    private static final long serialVersionUID = 1L;
    private int id;
    private int cvv;
    private int yearOfExpiry;
    private int monthOfExpiry;
    private int pin;
    private long bankCardNumber;
    private String bankCardType;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankAccountNumber;
    @ManyToOne
    private BankUser bankUser;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCvv() {
        return cvv;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public int getYearOfExpiry() {
        return yearOfExpiry;
    }

    public void setYearOfExpiry(int yearOfExpiry) {
        this.yearOfExpiry = yearOfExpiry;
    }

    public int getMonthOfExpiry() {
        return monthOfExpiry;
    }

    public void setMonthOfExpiry(int monthOfExpiry) {
        this.monthOfExpiry = monthOfExpiry;
    }

    public String getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(String bankCardType) {
        this.bankCardType = bankCardType;
    }

    public Long getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(Long bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public BankUser getBankUser() {
        return bankUser;
    }

    public long getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(long bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public void setBankUser(BankUser bankUser) {
        this.bankUser = bankUser;
    }
}
