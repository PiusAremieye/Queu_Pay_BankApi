package com.dummy.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "BankCards")
@Entity
public class BankTransactionDetails extends AuditModel {

  private double credit;
  private double amount;
  private double debit;
  private double totalCredit;
  private double totalDebit;
  private double totalTransaction;
  private double accountBalance;
  @ManyToOne
  private BankCard bankCard;
}
