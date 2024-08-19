package com.example.services.implementations;

import com.example.exceptions.UserNotFoundException;
import com.example.persistence.entities.User;
import com.example.persistence.repositories.UserRepository;
import com.example.services.interfaces.GetCurrentUser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GetCurrentUserService implements GetCurrentUser {

  private final UserRepository userRepository;

  public GetCurrentUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public User getUser() {
    val username = SecurityContextHolder.getContext().getAuthentication().getName();
    val optionalUser = userRepository.findByUsername(username);
    if (optionalUser.isEmpty()) {
      log.error("User with username '%s' not found in database.".formatted(username));
      throw new UserNotFoundException("User with username '%s' not found in database.".formatted(username));
    }
    return optionalUser.get();
  }
}
