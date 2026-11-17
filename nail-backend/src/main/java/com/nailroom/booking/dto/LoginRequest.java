package com.nailroom.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Vui lòng nhập tài khoản")
    private String username;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;
}
