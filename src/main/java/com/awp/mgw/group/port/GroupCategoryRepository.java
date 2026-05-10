package com.awp.mgw.group.port;

import com.awp.mgw.group.domain.Group;
import com.awp.mgw.group.domain.GroupCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupCategoryRepository extends JpaRepository<GroupCategory, Long> {
    void deleteAllByGroup(Group group);
}
