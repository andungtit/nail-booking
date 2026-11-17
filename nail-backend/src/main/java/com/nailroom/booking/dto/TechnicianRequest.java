package com.nailroom.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechnicianRequest {

    @NotNull(message = "Vui lòng chọn chi nhánh")
    private Integer locationId;

    @NotBlank(message = "Tên thợ không được để trống")
    @Size(max = 255)
    private String fullName;

    @Size(max = 500, message = "URL ảnh tối đa 500 ký tự")
    private String avatarUrl;

    @Size(max = 50)
    private String phone;

    @Pattern(regexp = "active|off|busy", message = "Trạng thái phải là active, off hoặc busy")
    private String status = "active";
}
