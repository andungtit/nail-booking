package com.nailroom.booking.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeFrameResponse {

    @Builder.Default
    private int status = 1;

    private TimeFrameData timeFrame;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeFrameData {
        private WorkingHours workingHours;
        @Builder.Default
        private List<TimeSlot> timeFrame = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkingHours {
        @JsonProperty("day_of_week")
        private Integer dayOfWeek;

        @JsonProperty("is_working")
        @Builder.Default
        private int isWorking = 1;

        @JsonProperty("start_time")
        @Builder.Default
        private String startTime = "08:00:00";

        @JsonProperty("end_time")
        @Builder.Default
        private String endTime = "22:00:00";

        @JsonProperty("is_break")
        @Builder.Default
        private int isBreak = 0;

        @JsonProperty("break_start_time")
        private String breakStartTime;

        @JsonProperty("break_end_time")
        private String breakEndTime;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot {
        private String time;
        private int count;
        private int isSlotFull;
    }
}
