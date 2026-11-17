package com.nailroom.booking.dto.booking;

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
public class BookingCreateResponse {

    private int status = 1;

    private BookingData data;

    private String message;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingData {
        @JsonProperty("booking_code")
        private String bookingCode;

        @JsonProperty("appointment_id")
        private Integer appointmentId;
    }
}
