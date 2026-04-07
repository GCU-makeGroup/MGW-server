package com.awp.mgw.member.service;

import com.awp.mgw.common.exception.CommonException;
import com.awp.mgw.global.exception.constant.CommonErrorCode;
import com.awp.mgw.member.domain.Member;
import com.awp.mgw.member.dto.MemberRequest;
import com.awp.mgw.member.dto.MemberResponse;
import com.awp.mgw.member.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse create(MemberRequest.Create request) {
        memberRepository.insert(
            request.email(),
            request.name(),
            request.imageUrl(),
            request.introduction(),
            request.point() == null ? 0 : request.point()
        );

        Long memberId = memberRepository.findLastInsertId();
        return MemberResponse.from(getMemberEntity(memberId));
    }

    public MemberResponse getMember(Long memberId) {
        return MemberResponse.from(getMemberEntity(memberId));
    }

    public List<MemberResponse> getMembers() {
        return memberRepository.findAllActive().stream()
            .map(MemberResponse::from)
            .toList();
    }

    @Transactional
    public MemberResponse update(Long memberId, MemberRequest.Update request) {
        int updated = memberRepository.update(
            memberId,
            request.name(),
            request.imageUrl(),
            request.introduction(),
            request.point() == null ? 0 : request.point()
        );

        if (updated == 0) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "수정할 멤버를 찾을 수 없습니다.");
        }

        return MemberResponse.from(getMemberEntity(memberId));
    }

    @Transactional
    public void delete(Long memberId) {
        int deleted = memberRepository.softDelete(memberId);
        if (deleted == 0) {
            throw new CommonException(CommonErrorCode.NOT_FOUND, "삭제할 멤버를 찾을 수 없습니다.");
        }
    }

    private Member getMemberEntity(Long memberId) {
        return memberRepository.findActiveById(memberId)
            .orElseThrow(() -> new CommonException(CommonErrorCode.NOT_FOUND, "멤버를 찾을 수 없습니다."));
    }
}
