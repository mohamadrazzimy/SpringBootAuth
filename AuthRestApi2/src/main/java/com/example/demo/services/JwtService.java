package com.example.demo.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final Logger logger = LoggerFactory.getLogger(
    JwtService.class
  );

  @Value("${security.jwt.secret}")
  private String secretKey; // Secret key for signing the JWT

  @Value("${security.jwt.expiration}")
  private long jwtExpiration; // Expiration time for JWT in milliseconds

  // Extracts the username from the token
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Extracts specific claims from the token using a claims resolver
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // Generates a token for the given user details
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  // Generates a token with additional claims
  public String generateToken(
    Map<String, Object> extraClaims,
    UserDetails userDetails
  ) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  // Returns the expiration time
  public long getExpirationTime() {
    return jwtExpiration;
  }

  // Builds the JWT token
  private String buildToken(
    Map<String, Object> extraClaims,
    UserDetails userDetails,
    long expiration
  ) {
    return Jwts
      .builder()
      .setClaims(extraClaims)
      .setSubject(userDetails.getUsername())
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + expiration))
      .signWith(getSignInKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  // Validates the token against the user details
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    boolean isValid =
      username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    logger.info("Token validation for user {}: {}", username, isValid);
    return isValid;
  }

  // Checks if the token is expired
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // Extracts the expiration date from the token
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  // Extracts all claims from the token
  private Claims extractAllClaims(String token) {
    try {
      return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    } catch (Exception e) {
      logger.error("Failed to extract claims from token: {}", e.getMessage());
      return null; // Consider throwing a custom exception instead of returning null
    }
  }

  // Retrieves the signing key
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
