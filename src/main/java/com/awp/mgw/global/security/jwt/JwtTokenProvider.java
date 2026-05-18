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
  private static final String EMAIL_CLAIM = "email";
  private static final String TOKEN_TYPE_CLAIM = "type";
  private static final String ACCESS_TOKEN_TYPE = "ACCESS";
  private static final String REFRESH_TOKEN_TYPE = "REFRESH";

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
    return createToken(memberId, email, ACCESS_TOKEN_TYPE, accessTokenExpiration);
  }

  public String createRefreshToken(Long memberId, String email) {
    return createToken(memberId, email, REFRESH_TOKEN_TYPE, refreshTokenExpiration);
  }

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean validateRefreshToken(String token) {
    try {
      Claims claims = parseClaims(token);
      return REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public boolean isAccessToken(String token) {
    try {
      Claims claims = parseClaims(token);
      return ACCESS_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public Long getMemberId(String token) {
    return Long.valueOf(parseClaims(token).getSubject());
  }

  public String getEmail(String token) {
    return parseClaims(token).get(EMAIL_CLAIM, String.class);
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();
  }

  private String createToken(Long memberId, String email, String tokenType, long expiration) {
    Date now = new Date();
    Date expiredAt = new Date(now.getTime() + expiration);

    return Jwts.builder()
          .setSubject(String.valueOf(memberId))
          .claim(EMAIL_CLAIM, email)
          .claim(TOKEN_TYPE_CLAIM, tokenType)
          .setIssuedAt(now)
          .setExpiration(expiredAt)
          .signWith(key, SignatureAlgorithm.HS256)
          .compact();
  }
}