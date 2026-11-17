package com.nailroom.booking.dto;

import com.nailroom.booking.dto.base.BaseListRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ListSchedulesRequest extends BaseListRequest {
    private String q;
    private Integer technicianId;
    private Integer locationId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;

    private Boolean isOff;
}
