package com.awp.mgw.activity.service;

import com.awp.mgw.activity.controller.dto.response.ActivityListResponse;
import com.awp.mgw.activity.domain.exception.ActivityDomainException;
import com.awp.mgw.activity.port.ActivityQueryRepository;
import com.awp.mgw.activity.port.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityQueryServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private ActivityQueryRepository activityQueryRepository;

    @InjectMocks
    private ActivityQueryService activityQueryService;

    @Test
    void getActivityListUsesRequestedLimitForPaging() {
        when(activityQueryRepository.findTopHotpick(1L, null)).thenReturn(null);
        when(activityQueryRepository.findActivitySummaries(1L, null, null, 30L, 6))
            .thenReturn(List.of(
                row(29L, "a"),
                row(28L, "b"),
                row(27L, "c")
            ));

        ActivityListResponse response = activityQueryService.getActivityList(1L, null, null, 5, 30L);

        verify(activityQueryRepository).findActivitySummaries(1L, null, null, 30L, 6);
        assertThat(response.activities()).hasSize(3);
        assertThat(response.nextCursor()).isNull();
    }

    @Test
    void searchActivitiesReturnsNextCursorUsingLimit() {
        when(activityQueryRepository.searchActivitySummaries(1L, "러닝", 100L, 3))
            .thenReturn(List.of(
                row(99L, "러닝 1"),
                row(98L, "러닝 2"),
                row(97L, "러닝 3")
            ));
        when(activityQueryRepository.findTopHotpick(1L, null)).thenReturn(null);

        ActivityListResponse response = activityQueryService.searchActivities(1L, "러닝", 2, 100L);

        verify(activityQueryRepository).searchActivitySummaries(1L, "러닝", 100L, 3);
        assertThat(response.activities()).hasSize(2);
        assertThat(response.nextCursor()).isEqualTo("98");
    }

    @Test
    void searchActivitiesThrowsWhenKeywordBlank() {
        assertThatThrownBy(() -> activityQueryService.searchActivities(1L, " ", 20, null))
            .isInstanceOf(ActivityDomainException.class);
    }

    private ActivityQueryRepository.ActivitySummaryRow row(Long id, String title) {
        return new ActivityQueryRepository.ActivitySummaryRow(
            id,
            title,
            "운동",
            10,
            1L,
            false,
            0L,
            Instant.parse("2026-05-10T00:00:00Z"),
            "thumbnail",
            1L
        );
    }
}
