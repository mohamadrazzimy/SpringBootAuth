package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data; // Automatically generates getter, setter, toString, equals, and hashCode
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data // This single annotation replaces @Getter, @Setter, and others
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Empty constructor for JPA
    public User() {}

    // Implement UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // Customize based on your roles/permissions
    }

    @Override
    public String getUsername() {
        return email; // Use email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize based on your logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Customize based on your logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize based on your logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Customize based on your logic
    }
}