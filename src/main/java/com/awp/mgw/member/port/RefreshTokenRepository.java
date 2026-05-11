package com.awp.mgw.member.port;

import com.awp.mgw.member.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByMemberId(Long memberId);

  void deleteByMemberId(Long memberId);
}