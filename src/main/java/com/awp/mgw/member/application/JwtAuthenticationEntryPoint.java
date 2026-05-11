package com.awp.mgw.member.application.security;

import com.awp.mgw.global.exception.constant.CommonErrorCode;
import com.awp.mgw.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
  ) throws IOException {
    CommonErrorCode errorCode = CommonErrorCode.UNAUTHORIZED;

    response.setStatus(errorCode.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    ApiResponse<Object> body = ApiResponse.onFailure(
          errorCode.getCode(),
          errorCode.getMessage(),
          null
    );

    response.getWriter().write(objectMapper.writeValueAsString(body));
  }
}