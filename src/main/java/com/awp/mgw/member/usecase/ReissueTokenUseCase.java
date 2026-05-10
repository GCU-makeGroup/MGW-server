package com.awp.mgw.member.usecase.command;

import com.awp.mgw.member.controller.dto.response.TokenReissueResponse;

public interface ReissueTokenUseCase {

  TokenReissueResponse reissue(String refreshToken);
}