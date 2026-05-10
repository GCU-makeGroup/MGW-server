package com.awp.mgw.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

  private final Key key;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;

  public JwtTokenProvider(
        @Value("${jwt.secret}") String secretKey,
        @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
        @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
  ) {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
  }

  public String createAccessToken(Long memberId, String email) {
    return createToken(memberId, email, accessTokenExpiration);
  }

  public String createRefreshToken(Long memberId, String email) {
    return createToken(memberId, email, refreshTokenExpiration);
  }

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public Long getMemberId(String token) {
    return Long.valueOf(parseClaims(token).getSubject());
  }

  public String getEmail(String token) {
    return parseClaims(token).get("email", String.class);
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();
  }

  private String createToken(Long memberId, String email, long expiration) {
    Date now = new Date();
    Date expiredAt = new Date(now.getTime() + expiration);

    return Jwts.builder()
          .setSubject(String.valueOf(memberId))
          .claim("email", email)
          .setIssuedAt(now)
          .setExpiration(expiredAt)
          .signWith(key, SignatureAlgorithm.HS256)
          .compact();
  }
}