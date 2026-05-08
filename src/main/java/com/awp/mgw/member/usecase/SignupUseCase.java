package com.awp.mgw.member.usecase;

import com.awp.mgw.member.controller.dto.request.SignupRequest;
import com.awp.mgw.member.controller.dto.response.SignupResponse;

public interface SignupUseCase {

  SignupResponse signup(SignupRequest request);
}