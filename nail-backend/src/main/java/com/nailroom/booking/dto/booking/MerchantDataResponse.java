package com.nailroom.booking.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDataResponse {

    private int id = 1;
    private String name = "Nail Room - Mit's House";

    @JsonProperty("logo_img")
    private String logoImg = "";

    private String phone = "1900 066 811";
    private String facebook = "https://www.facebook.com/nailroom.mitshouse";
    private String website = "https://nailroom.vn";
    private String type = "nail_salon";
    private String currency = "₫";

    @JsonProperty("gender_type")
    private String genderType = "all";

    @JsonProperty("use_email4booking")
    private int useEmail4Booking = 0;

    private String timezone = "+07:00";

    @JsonProperty("date_format")
    private String dateFormat = "DD/MM/YYYY";

    @JsonProperty("time_format")
    private String timeFormat = "HH:mm";

    private String subdomain = "nailroom";

    @JsonProperty("cover_image_url")
    private String coverImageUrl = "";

    private Map<String, Object> setting = new HashMap<>();

    private List<Map<String, Object>> location = new ArrayList<>();

    @JsonProperty("product_groups")
    private List<Map<String, Object>> productGroups = new ArrayList<>();

    private List<Map<String, Object>> product = new ArrayList<>();

    private Map<String, Map<String, Object>> workingHours = new HashMap<>();

    private List<Map<String, Object>> operator = new ArrayList<>();
}
