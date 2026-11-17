package com.nailroom.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class BulkScheduleRequest {

    @NotEmpty(message = "Vui lòng chọn ít nhất 1 thợ")
    private List<Integer> technicianIds;

    @NotNull(message = "Vui lòng chọn ngày bắt đầu")
    private LocalDate fromDate;

    @NotNull(message = "Vui lòng chọn ngày kết thúc")
    private LocalDate toDate;

    private List<Integer> weekdays;

    @NotNull(message = "Vui lòng nhập giờ bắt đầu")
    private LocalTime startTime;

    @NotNull(message = "Vui lòng nhập giờ kết thúc")
    private LocalTime endTime;

    @JsonProperty("isOff")
    private Boolean isOff = false;

    private Boolean overwrite = true;
}
