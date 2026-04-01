package com.awp.mgw.group.port;

import com.awp.mgw.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 단순 CRUD 용 레포지토리
 */
public interface GroupRepository extends JpaRepository<Group, Long> {

    // 기본적으로 제공되는 save(), findById(), deleteById() 등을 그대로 사용합니다.

    // 필요하다면 아주 간단한 메서드 이름 쿼리 정도만 추가합니다.
    boolean existsByName(String name);
}
