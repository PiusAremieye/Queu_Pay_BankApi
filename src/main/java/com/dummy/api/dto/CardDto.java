package com.dummy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CardDto {
  private int cvv;
  private int yearOfExpiry;
  private int monthOfExpiry;
  private int pin;
  private long bankCardNumber;
  private String bankCardType;
}
