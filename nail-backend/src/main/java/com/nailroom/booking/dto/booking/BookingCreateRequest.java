package com.nailroom.booking.dto.booking;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookingCreateRequest {

    @JsonProperty("location_id")
    @JsonAlias({"locationId"})
    private Integer locationId;

    @JsonProperty("merchant_id")
    @JsonAlias({"merchantId"})
    private Integer merchantId;

    @JsonProperty("customer_id")
    @JsonAlias({"customerId"})
    private Integer customerId;

    private String gender;

    @JsonProperty("product_id")
    @JsonAlias({"productId", "product_id[]"})
    private List<Integer> productId = new ArrayList<>();

    @JsonProperty("booked_at")
    @JsonAlias({"bookedAt"})
    private String bookedAt;

    @JsonProperty("operator_id")
    @JsonAlias({"operatorId", "operator_id[]"})
    private List<Integer> operatorId = new ArrayList<>();

    private String note;

    @JsonProperty("customer_name")
    @JsonAlias({"customerName"})
    private String customerName;

    private String mobile;

    @JsonProperty("phone_country_code")
    @JsonAlias({"phoneCountryCode"})
    private String phoneCountryCode;

    private String email;

    @JsonProperty("booking_code")
    @JsonAlias({"bookingCode"})
    private String bookingCode;

    @JsonProperty("number_of_guests")
    @JsonAlias({"numberOfGuests", "guests"})
    private Integer numberOfGuests = 1;

    @JsonProperty("city_code")
    @JsonAlias({"cityCode"})
    private Integer cityCode;

    @JsonProperty("district_code")
    @JsonAlias({"districtCode"})
    private Integer districtCode;

    @JsonProperty("estimated_duration")
    @JsonAlias({"estimatedDuration"})
    private Integer estimatedDuration = 60;

    @JsonProperty("externalUserId")
    private String externalUserId;

    @JsonProperty("externalAppId")
    private String externalAppId;
}
