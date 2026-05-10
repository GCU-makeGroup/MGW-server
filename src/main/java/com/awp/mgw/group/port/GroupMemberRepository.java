package com.awp.mgw.group.port;

import com.awp.mgw.group.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    boolean existsByMember_IdAndGroup_Id(Long memberId, Long groupId);

    Optional<GroupMember> findByMember_IdAndGroup_Id(Long memberId, Long groupId);

    long countByGroup_Id(Long groupId);
}
