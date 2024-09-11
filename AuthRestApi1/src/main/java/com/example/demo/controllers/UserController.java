package com.example.demo.controllers;

import com.example.demo.dtos.UserDto;
import com.example.demo.entities.User;
import com.example.demo.services.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public ResponseEntity<String> authenticatedUser(
    @RequestHeader(
      value = "Authorization",
      required = false
    ) String authorizationHeader
  ) {
    // Check if the Authorization header is present
    if (
      authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")
    ) {
      return ResponseEntity.status(401).build(); // Unauthorized
    }

    // Extract the token
    String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

    // For demonstration, we just print the token
    System.out.println("Token: " + token);
    return ResponseEntity.ok("Token: " + token);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
    Optional<UserDto> userDto = userService.findByIdAsDto(id);
    return userDto
      .map(ResponseEntity::ok)
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/all")
  public ResponseEntity<List<UserDto>> getAllUsers() {
    List<UserDto> users = userService.findAllAsDto();
    return ResponseEntity.ok(users);
  }
}
