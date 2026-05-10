package com.awp.mgw.activity.port;

import com.awp.mgw.activity.domain.Activity;
import com.awp.mgw.activity.domain.ActivityCategory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Long> {
    @Modifying
    void deleteAllByActivity(Activity activity);
}
