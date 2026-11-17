package com.nailroom.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TechnicianScheduleRequest {

    @NotNull(message = "Vui lòng chọn thợ")
    private Integer technicianId;

    @NotNull(message = "Vui lòng chọn ngày làm việc")
    private LocalDate workDate;

    @NotNull(message = "Vui lòng nhập giờ bắt đầu")
    private LocalTime startTime;

    @NotNull(message = "Vui lòng nhập giờ kết thúc")
    private LocalTime endTime;

    @JsonProperty("isOff")
    private Boolean isOff = false;
}
