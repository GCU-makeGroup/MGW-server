package com.awp.mgw.notification.port;

import com.awp.mgw.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<Notification> findByIdAndMemberId(Long id, Long memberId);
    long countByMemberIdAndIsReadFalse(Long memberId);
}
