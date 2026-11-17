package com.nailroom.booking.dto.booking;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeFrameRequest {

    @JsonProperty("merchant_id")
    @JsonAlias({"merchantId"})
    private Integer merchantId;

    @JsonProperty("location_id")
    @JsonAlias({"locationId"})
    private Integer locationId;

    private String date; // YYYY-MM-DD

    @JsonProperty("operator_id")
    @JsonAlias({"operatorId"})
    private Integer operatorId;
}
