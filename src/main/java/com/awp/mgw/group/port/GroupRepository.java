package com.awp.mgw.group.port;

import com.awp.mgw.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 단순 CRUD 용 레포지토리
 */
public interface GroupRepository extends JpaRepository<Group, Long> {

}
