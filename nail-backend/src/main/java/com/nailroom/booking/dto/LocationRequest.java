package com.nailroom.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationRequest {

    @NotBlank(message = "Tên chi nhánh không được để trống")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Thành phố không được để trống")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 50)
    private String phone;

    @JsonProperty("isActive")
    private Boolean isActive = true;
}
