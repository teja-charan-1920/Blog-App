package com.mountblue.Service;

import com.mountblue.dtos.LoginDtoForRest;
import com.mountblue.dtos.RegisterUserDto;
import com.mountblue.model.Posts;
import com.mountblue.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Data
@Service
@AllArgsConstructor
public class RestAPIService {
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final PostsService postsService;

    public boolean validateTheHeader(String authorizationHeader){
        String[] userNameAndPassWord = base64ToString(authorizationHeader).split(":");
        if(userNameAndPassWord.length < 1) return false;
        LoginDtoForRest loginDtoForRest = new LoginDtoForRest(userNameAndPassWord[0],userNameAndPassWord[1]);
        return validateTheUser(loginDtoForRest);
    }

    private boolean validateTheUser(LoginDtoForRest loginDtoForRest) {
        User user = userService.findByEmail(loginDtoForRest.getUserName());
        if(encoder.matches(loginDtoForRest.getPassWord(), user.getPassWord())){
            return true;
        } else {
            return false;
        }
    }
    public String base64ToString(String authorizationHeader) {
        String[] parts = authorizationHeader.split(" ");
        if (parts.length == 2 && parts[0].equalsIgnoreCase("Basic")) {
            byte[] decodedByteArray = Base64.getDecoder().decode(parts[1]);
            return new String(decodedByteArray, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }


    public boolean validateTheUserForPost(String authorDetail, int postId) {
        Posts post = null;
        try {
            post = postsService.getPostById(postId);
        } catch (Exception e) {
            return false;
        }
        User user = userService.findByEmail(authorDetail);

        String[] roles = user.getRoles().split(",");
        boolean isAdmin = false;
        for(String role : roles){
            if(role.equals("ROLE_ADMIN"))
                isAdmin = true;
        }
        return post.getAuthor().equals(user.getName()) || isAdmin;
    }
}
