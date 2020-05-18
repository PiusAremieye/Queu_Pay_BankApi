package com.dummy.api.dto;

import com.dummy.api.models.BankCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferDto {
  private Double amount;
  private Long accountNumber;
}
