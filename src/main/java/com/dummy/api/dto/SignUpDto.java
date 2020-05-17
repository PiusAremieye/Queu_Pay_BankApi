package com.dummy.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignUpDto {
  private String lastName;
  private String firstName;
  private String username;
  private String password;
  private String phoneNumber;
  private String address;
}
