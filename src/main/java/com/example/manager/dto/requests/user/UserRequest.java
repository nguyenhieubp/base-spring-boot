package com.example.manager.dto.requests.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserRequest {
    @NotEmpty(message = "Tên người dùng không được bỏ trống")
    private String userName;

    @NotEmpty(message = "Mật khẩu không được trống") // Ensures password is not empty
    @Size(min = 6, message = "Mật khẩu cần ít nhất là 6 ký tự") // Enforces a minimum password length
    private String password;
}
