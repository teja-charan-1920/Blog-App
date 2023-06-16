package com.mountblue.Service;

import com.mountblue.Repos.UserRepository;
import com.mountblue.model.RegisterUser;
import com.mountblue.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addUser(User user) throws Exception {
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }

    public void saveTheUser(RegisterUser registerUser) throws Exception {
        User user = new User();
        user.setName(registerUser.getName());
        user.setEmail(registerUser.getEmail());
        user.setPassWord(passwordEncoder.encode(registerUser.getPassWord()));
        user.setRoles("ROLE_AUTHOR");
        userRepository.save(user);
    }
}
