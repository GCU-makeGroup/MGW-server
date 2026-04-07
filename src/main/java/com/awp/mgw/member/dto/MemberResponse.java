package com.awp.mgw.member.dto;

import com.awp.mgw.member.domain.Member;

import java.time.LocalDateTime;

public record MemberResponse(
    Long id,
    Long email,
    String name,
    String imageUrl,
    String introduction,
    Integer point,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime deletedAt
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getImageUrl(),
            member.getIntroduction(),
            member.getPoint(),
            member.getCreatedAt(),
            member.getUpdatedAt(),
            member.getDeletedAt()
        );
    }
}
