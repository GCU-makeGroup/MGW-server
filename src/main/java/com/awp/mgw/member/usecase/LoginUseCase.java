package com.awp.mgw.member.usecase;

import com.awp.mgw.member.controller.dto.request.LoginRequest;
import com.awp.mgw.member.controller.dto.response.LoginResponse;

public interface LoginUseCase {

  LoginResponse login(LoginRequest request);
}