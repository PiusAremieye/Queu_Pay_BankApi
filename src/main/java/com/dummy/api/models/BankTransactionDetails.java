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

  private Double credit;
  private Double amount;
  private Double debit;
  private Double accountBalance;
  @ManyToOne
  private BankCard bankCard;
}
