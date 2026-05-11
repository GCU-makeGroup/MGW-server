package com.awp.mgw.member.port;

import com.awp.mgw.member.domain.EmailVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

  Optional<EmailVerification> findByEmail(String email);

  boolean existsByEmailAndVerifiedTrue(String email);
}