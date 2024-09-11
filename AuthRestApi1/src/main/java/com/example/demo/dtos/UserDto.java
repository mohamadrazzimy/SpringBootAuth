package com.example.demo.dtos;

public class UserDto {

  private Long id;
  private String fullName;
  private String email;

  // Constructor
  public UserDto(Long id, String fullName, String email) {
    this.id = id;
    this.fullName = fullName;
    this.email = email;
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public String getEmail() {
    return email;
  }
}
