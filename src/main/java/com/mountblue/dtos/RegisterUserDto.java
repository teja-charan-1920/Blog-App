package com.mountblue.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserDto {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
}
