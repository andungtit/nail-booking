package com.nailroom.booking.dto.booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLookupResponse {

    private int status = 1;

    private CustomerData data;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CustomerData {
        private Integer id;

        @JsonProperty("full_name")
        private String fullName;

        private String gender;

        private String email;

        private String mobile;

        @JsonProperty("phone_country_code")
        private String phoneCountryCode;
    }
}
