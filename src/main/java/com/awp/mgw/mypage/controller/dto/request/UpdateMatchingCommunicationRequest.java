package com.awp.mgw.mypage.controller.dto.request;

import com.awp.mgw.member.domain.enums.Language;

import java.util.List;

public record UpdateMatchingCommunicationRequest(
        List<String> interestKeywords,
        Language preferredLanguage
) {
}
