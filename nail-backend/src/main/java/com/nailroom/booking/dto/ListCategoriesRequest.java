package com.nailroom.booking.dto;

import com.nailroom.booking.dto.base.BaseListRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListCategoriesRequest extends BaseListRequest {
    private String q;
}
