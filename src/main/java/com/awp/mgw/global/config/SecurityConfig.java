package com.awp.mgw.global.config;

import com.awp.mgw.member.application.security.JwtAuthenticationEntryPoint;
import com.awp.mgw.member.application.security.JwtAuthenticationFilter;
import com.awp.mgw.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);

    return http
          .cors(Customizer.withDefaults())
          .csrf(csrf -> csrf.disable())
          .formLogin(form -> form.disable())
          .httpBasic(httpBasic -> httpBasic.disable())
          .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
          .exceptionHandling(exception ->
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
          )
          .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                      "/api/v1/auth/signup",
                      "/api/v1/auth/login",
                      "/api/v1/auth/token/reissue",
                      "/api/v1/auth/email-verification/send",
                      "/api/v1/auth/email-verification/resend",
                      "/api/v1/auth/email-verification/verify",
                      "/swagger-ui.html",
                      "/swagger-ui/**",
                      "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
          )
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          .build();
  }
}
