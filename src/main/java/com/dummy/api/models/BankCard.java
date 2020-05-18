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
public class BankCard extends AuditModel {

  private Integer cvv;
  private Integer yearOfExpiry;
  private Integer monthOfExpiry;
  private Integer pin;
  private Long bankCardNumber;
  private Long accountNumber;
  private String bankCardType;

  @ManyToOne
  private BankUser bankUser;
}
