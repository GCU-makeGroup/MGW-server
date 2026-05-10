package com.awp.mgw.activity.port;

import com.awp.mgw.activity.domain.ActivityGroup;
import com.awp.mgw.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityGroupRepository extends JpaRepository<ActivityGroup, Long> {
    void deleteAllByGroup(Group group);
}
