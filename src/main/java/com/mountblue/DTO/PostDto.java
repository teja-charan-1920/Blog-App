package com.mountblue.DTO;

import com.mountblue.Service.PostsService;
import com.mountblue.Service.UserService;
import com.mountblue.model.Posts;
import com.mountblue.model.Tags;
import com.mountblue.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@Component
public class PostDto {
    private final PostsService postsService;
    private final UserService userService;

    public Posts savePostData(Posts posts, List<Tags> tagsList) {
//        String authors[] = {"Charan", "Yaswanth", "Gopal", "Surendra", "Vignesh", "Rahul", "Chandra", "Bhanu", "Nikhil", "Cherry", "Madhan", "Vinay"};
//        Random random = new Random();
        Posts post = new Posts();
        post.setPostId(posts.getPostId());
        post.setTitle(posts.getTitle());
        post.setContent(posts.getContent());
        if (posts.getContent().length() < 255) {
            post.setExcerpt(post.getContent());
        } else {
            post.setExcerpt(post.getContent().substring(0, 254));
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) principal).getUsername();

        User user = userService.findByEmail(email);
        post.setAuthor(user.getName());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        post.setPublishedAt((Timestamp) posts.getPublishedAt());
        post.setCreatedAt((Timestamp) posts.getCreatedAt());
        post.setPublished(true);
        post.setUpdatedAt(timestamp);
        post.setTagsList(tagsList);
        return post;

    }

    public String getPostTagsData(int id) {
        Posts posts = postsService.getTheSpecificPost(id);
        List<Tags> tagsList = posts.getTagsList();
        String tag = "";
        for (Tags tags : tagsList) {
            tag += tags.getName() + ",";
        }
        return tag;
    }
}
