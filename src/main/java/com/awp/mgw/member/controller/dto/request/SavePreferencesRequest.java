package com.awp.mgw.member.controller.dto.request;

import java.util.List;

public record SavePreferencesRequest(
        List<String> interestKeywords,
        String purpose
) {
}
