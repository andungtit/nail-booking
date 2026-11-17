package com.nailroom.booking.dto;

import com.nailroom.booking.dto.base.BaseListRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListLocationsRequest extends BaseListRequest {
    private String q;
    private String city;
    private Boolean isActive;
}
