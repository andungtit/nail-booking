package com.nailroom.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceCategoryRequest {

    @NotBlank(message = "Tên nhóm dịch vụ không được để trống")
    @Size(max = 255)
    private String name;

    @Min(value = 0, message = "Thứ tự phải >= 0")
    private Integer sortOrder = 0;
}
