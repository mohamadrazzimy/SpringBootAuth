package com.example.demo.controllers;

import com.example.demo.dtos.LoginResponseDto;
import com.example.demo.dtos.LoginUserDto;
import com.example.demo.dtos.RegisterUserDto;
import com.example.demo.entities.User;
import com.example.demo.services.AuthenticationService;
import com.example.demo.services.JwtService;/*add*/
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final JwtService jwtService;/*add*/

  public AuthenticationController(
    JwtService jwtService/*add*/,
    AuthenticationService authenticationService
  ) {
    this.authenticationService = authenticationService;
    this.jwtService = jwtService;/*add*/
  }

  @PostMapping("/signup")
  public ResponseEntity<User> register(
    @Valid @RequestBody RegisterUserDto registerUserDto
  ) {
    User registeredUser = authenticationService.signup(registerUserDto);
    return ResponseEntity.ok(registeredUser);
  }

  // Endpoint for user login
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> authenticate(
    @RequestBody LoginUserDto loginUserDto
  ) {
    User authenticatedUser = authenticationService.authenticate(loginUserDto);

    // Generate JWT token for the authenticated user
    String jwtToken = jwtService.generateToken(authenticatedUser);
    LoginResponseDto loginResponse = new LoginResponseDto()
      .setToken(jwtToken)
      .setExpiresIn(jwtService.getExpirationTime());

    return ResponseEntity.ok(loginResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
    MethodArgumentNotValidException ex
  ) {
    Map<String, String> errors = new HashMap<>();
    ex
      .getBindingResult()
      .getAllErrors()
      .forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
      });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}
