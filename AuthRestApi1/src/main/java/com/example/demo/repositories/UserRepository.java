package com.example.demo.repositories;

import com.example.demo.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  /**
   * Finds a user by their email.
   *
   * @param email the email of the user
   * @return an Optional containing the user if found, or empty if not
   */
  Optional<User> findByEmail(String email);
}
