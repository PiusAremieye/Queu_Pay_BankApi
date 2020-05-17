package com.dummy.api.Service;

import com.dummy.api.exception.CustomException;
import com.dummy.api.models.BankUser;
import com.dummy.api.repository.BankUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class MyUserDetailsService implements UserDetailsService {

  private BankUserRepository bankUserRepository;

  @Autowired
  public MyUserDetailsService(BankUserRepository bankUserRepository) {
    this.bankUserRepository = bankUserRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return bankUserRepository.findByUsername(username)
            .orElseThrow(() -> new CustomException("Email " + username + " was not found", HttpStatus.NOT_FOUND));
  }

  @Transactional
  public BankUser loadByEmail(String username){
    return bankUserRepository.findByUsername(username)
      .orElseThrow(() -> new CustomException("User " + username + " was not found", HttpStatus.NOT_FOUND));
  }
}
