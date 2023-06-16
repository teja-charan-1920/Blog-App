package com.mountblue.RestController;

import com.mountblue.Service.UserService;
import com.mountblue.dtos.RegisterUserDto;
import com.mountblue.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@AllArgsConstructor
@RestController
@RequestMapping("/api/blog")
public class UserRestController {
    private final UserService userService;
    private final PasswordEncoder encoder;

    @PostMapping("/saveUser")
    public ResponseEntity<String> saveUser(@RequestBody RegisterUserDto registerUserDto) {
        User user = new User();
        user.setName(registerUserDto.getName());
        user.setRoles("ROLE_AUTHOR");
        user.setEmail(registerUserDto.getEmail());
        user.setPassWord(encoder.encode(registerUserDto.getPassword()));
        try {
            userService.addUser(user);
        } catch (Exception e) {
            return new ResponseEntity<>("name or Email already exists Please Try again", HttpStatus.ALREADY_REPORTED);
        }
        return new ResponseEntity<>("Successfully register", HttpStatus.CREATED);
    }
}
