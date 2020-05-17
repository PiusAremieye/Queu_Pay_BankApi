package com.dummy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SmsRequest {

  @NotBlank
  private String phoneNumber;

  @NotBlank
  private String message;

  @Override
  public String toString() {
    return "SmsRequest{" +
      "phoneNumber='" + phoneNumber + '\'' +
      ", message='" + message + '\'' +
      '}';
  }
}
