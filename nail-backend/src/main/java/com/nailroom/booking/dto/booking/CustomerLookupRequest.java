package com.nailroom.booking.dto.booking;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerLookupRequest {

    @JsonProperty("merchant_id")
    @JsonAlias({"merchantId"})
    private Integer merchantId;

    private String mobile;

    private String email;
}
