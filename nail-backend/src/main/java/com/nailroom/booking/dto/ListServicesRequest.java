package com.nailroom.booking.dto;

import com.nailroom.booking.dto.base.BaseListRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListServicesRequest extends BaseListRequest {
    private String q;
    private Integer categoryId;
    private Boolean isActive;
}
