package com.dummy.api.Service.Implementation;

import com.dummy.api.Service.BankUserService;
import com.dummy.api.dto.SignUpDto;
import com.dummy.api.exception.CustomException;
import com.dummy.api.models.BankUser;
import com.dummy.api.repository.BankUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BankUserServiceImpl implements BankUserService {
  private BankUserRepository bankUserRepository;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public BankUserServiceImpl(BankUserRepository bankUserRepository, PasswordEncoder passwordEncoder) {
    this.bankUserRepository = bankUserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public BankUser createNewBankDetailsForUser(SignUpDto signUpDto) {
    if (!bankUserRepository.existsByUsername(signUpDto.getUsername())){
      BankUser bankUser = new BankUser();
      bankUser.setFirstName(signUpDto.getFirstName());
      bankUser.setLastName(signUpDto.getLastName());
      bankUser.setAddress(signUpDto.getAddress());
      bankUser.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
      bankUser.setPhoneNumber(signUpDto.getPhoneNumber());
      bankUser.setUsername(signUpDto.getUsername());
      bankUserRepository.save(bankUser);
      return bankUser;
    }
    throw new CustomException("Email already exists", HttpStatus.BAD_REQUEST);
  }
}
