package com.nailroom.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceRequest {

    @NotNull(message = "Vui lòng chọn nhóm dịch vụ")
    private Integer categoryId;

    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Size(max = 255)
    private String name;

    @NotNull(message = "Vui lòng nhập thời gian")
    @Min(value = 1, message = "Thời gian phải >= 1 phút")
    @Max(value = 1440, message = "Thời gian tối đa 1440 phút")
    private Integer durationMinutes;

    @NotNull(message = "Vui lòng nhập giá")
    @DecimalMin(value = "0", message = "Giá phải >= 0")
    @Digits(integer = 10, fraction = 2, message = "Giá không hợp lệ")
    private BigDecimal price;

    private String description;

    @JsonProperty("isActive")
    private Boolean isActive = true;
}
