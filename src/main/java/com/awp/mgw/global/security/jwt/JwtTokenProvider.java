package com.awp.mgw.global.security.jwt;

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