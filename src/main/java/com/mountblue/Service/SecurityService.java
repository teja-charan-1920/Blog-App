package com.mountblue.Service;

import com.mountblue.model.Posts;
import com.mountblue.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SecurityService {
    private final UserService userService;
    private final PostsService postsService;

    public boolean validateThePostOwner(int id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email);
        Posts post = postsService.getTheSpecificPost(id);
        boolean isAdmin = ((UserDetails) principal)
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        return post.getAuthor().equals(user.getName()) || isAdmin;
    }

}
