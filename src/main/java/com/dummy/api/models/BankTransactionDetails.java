package com.dummy.api.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class BankTransactionDetails implements Serializable {

    private static final long serialVersionUID =  1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long credit;
    private long amount;
    private long debit;
    private long totalCredit;
    private long totalDebit;
    private long totalTransaction;
    private long accountBalance;
    @ManyToOne
    private BankCard bankCard;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCredit() {
        return credit;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public long getDebit() {
        return debit;
    }

    public void setDebit(long debit) {
        this.debit = debit;
    }

    public long getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(long totalCredit) {
        this.totalCredit = totalCredit;
    }

    public long getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(long totalDebit) {
        this.totalDebit = totalDebit;
    }

    public long getTotalTransaction() {
        return totalTransaction;
    }

    public void setTotalTransaction(long totalTransaction) {
        this.totalTransaction = totalTransaction;
    }

    public long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
    }

    public BankCard getBankCard() {
        return bankCard;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void setBankCard(BankCard bankCard) {
        this.bankCard = bankCard;
    }
}
