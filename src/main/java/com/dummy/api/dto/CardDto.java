package com.dummy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CardDto {
  private Integer cvv;
  private Integer yearOfExpiry;
  private Integer monthOfExpiry;
  private Integer pin;
  private Long bankCardNumber;
  private Long accountNumber;
  private String bankCardType;
}
