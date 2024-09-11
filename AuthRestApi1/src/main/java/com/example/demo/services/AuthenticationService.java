package com.example.demo.services;

import com.example.demo.dtos.LoginUserDto;
import com.example.demo.dtos.RegisterUserDto;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  public AuthenticationService(
    UserRepository userRepository,
    AuthenticationManager authenticationManager,
    PasswordEncoder passwordEncoder
  ) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  public User signup(@Valid RegisterUserDto input) {
    // Check if user already exists
    if (userRepository.findByEmail(input.getEmail()).isPresent()) {
      throw new IllegalArgumentException(
        "User already exists with email: " + input.getEmail()
      );
    }

    // Create and save the new user
    String hashedPassword = passwordEncoder.encode(input.getPassword());
    User user = new User();
    user.setEmail(input.getEmail());
    user.setPassword(hashedPassword);
    user.setFullName(input.getFullName());

    return userRepository.save(user);
  }

  public User authenticate(LoginUserDto input) {
    User user = userRepository
      .findByEmail(input.getEmail())
      .orElseThrow(() ->
        new IllegalArgumentException("Email not found: " + input.getEmail())
      );

    // Check if password is correct
    if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException(
        "Incorrect password for email: " + input.getEmail()
      );
    }

    // Perform authentication
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        input.getEmail(),
        input.getPassword()
      )
    );

    return user; // Return authenticated user
  }

  public List<User> allUsers() {
    return userRepository.findAll();
  }
}
