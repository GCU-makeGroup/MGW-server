package com.awp.mgw.member.port;

import com.awp.mgw.member.domain.MemberSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberSettingRepository extends JpaRepository<MemberSetting, Long> {
    Optional<MemberSetting> findByMember_Id(Long memberId);
}
