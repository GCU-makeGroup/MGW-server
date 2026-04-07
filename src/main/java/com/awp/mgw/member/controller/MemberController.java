package com.awp.mgw.member.controller;

import com.awp.mgw.member.dto.MemberRequest;
import com.awp.mgw.member.dto.MemberResponse;
import com.awp.mgw.member.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member | 멤버", description = "멤버 CRUD API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public MemberResponse createMember(@Valid @RequestBody MemberRequest.Create request) {
        return memberService.create(request);
    }

    @GetMapping("/{memberId}")
    public MemberResponse getMember(@PathVariable Long memberId) {
        return memberService.getMember(memberId);
    }

    @GetMapping
    public List<MemberResponse> getMembers() {
        return memberService.getMembers();
    }

    @PutMapping("/{memberId}")
    public MemberResponse updateMember(
        @PathVariable Long memberId,
        @Valid @RequestBody MemberRequest.Update request
    ) {
        return memberService.update(memberId, request);
    }

    @DeleteMapping("/{memberId}")
    public void deleteMember(@PathVariable Long memberId) {
        memberService.delete(memberId);
    }
}
