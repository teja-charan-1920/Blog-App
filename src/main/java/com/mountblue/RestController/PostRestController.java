package com.mountblue.RestController;

import com.mountblue.DTO.PostDto;
import com.mountblue.DTO.PostsDto;
import com.mountblue.DTO.TagsDto;
import com.mountblue.Service.*;
import com.mountblue.dtos.PostDTOForRest;
import com.mountblue.model.Posts;
import com.mountblue.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/blog")
@Data
@AllArgsConstructor
public class PostRestController {
    private final PostsService postsService;
    private final TagsService tagsService;
    private final CommentsService commentsService;
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final PostDto postDto;
    private final TagsDto tagsDto;
    private final RestAPIService restAPIService;
    private final CommentsRestController commentsRestController;

    @GetMapping("/home")
    public ResponseEntity<List<PostDTOForRest>> getAllPosts() {
        List<Posts> allPosts = postsService.getAllPosts();
        List<PostDTOForRest> allPostsDto = new ArrayList<>();
        for (Posts post : allPosts) {
            PostDTOForRest postDTOForRest = new PostDTOForRest();
            postDTOForRest.setTitle(post.getTitle());
            postDTOForRest.setAuthor(post.getAuthor());
            postDTOForRest.setContent(post.getContent());
            postDTOForRest.setPublishedAt(post.getPublishedAt());
            postDTOForRest.setTagsForPost(postsService.getTagsForPost(post.getPostId()));
            postDTOForRest.setCommentsForPost(commentsService.getCommentsForPost(post.getPostId()));
            allPostsDto.add(postDTOForRest);
        }
        return new ResponseEntity<>(allPostsDto, HttpStatus.OK);
    }

    @GetMapping("/viewPost")
    public ResponseEntity<PostDTOForRest> getSpecificPost(@RequestParam("postId") int postId) {
        Posts post = postsService.getTheSpecificPost(postId);
        PostDTOForRest postDTOForRest = new PostDTOForRest();
        postDTOForRest.setTitle(post.getTitle());
        postDTOForRest.setAuthor(post.getAuthor());
        postDTOForRest.setContent(post.getContent());
        postDTOForRest.setPublishedAt(post.getPublishedAt());
        postDTOForRest.setTagsForPost(postsService.getTagsForPost(post.getPostId()));
        postDTOForRest.setCommentsForPost(commentsService.getCommentsForPost(post.getPostId()));
        return new ResponseEntity<>(postDTOForRest, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PostDTOForRest>> getPostsByFilter(@RequestParam(value = "filter1", required = false) String author,
                                                                 @RequestParam(value = "filter2", required = false) String publishedAt,
                                                                 @RequestParam(value = "filter3", required = false) String tag) {
        Set<Posts> postsByAuthor = null;
        Set<Posts> postsByPublishedAt = null;
        Set<Posts> postsByTags = null;
        List<PostDTOForRest> allPostsDto = new ArrayList<>();
        if (author != null) {
            postsByAuthor = postsService.getPostsByAuthor(author);
        }
        if (publishedAt != null) {
            postsByPublishedAt = postsService.getPostsByPublishedAt(publishedAt);
        }
        if (tag != null) {
            postsByTags = postsService.getPostsByTags(tag);
        }
        Set<Posts> postsByFilter = new HashSet<>();
        if (postsByAuthor == null && postsByPublishedAt == null && postsByTags == null) {
            return new ResponseEntity<>(allPostsDto, HttpStatus.OK);
        } else {
            if (postsByAuthor != null) {
                postsByFilter.addAll(postsByAuthor);
            }
            if (postsByPublishedAt != null) {
                postsByFilter.addAll(postsByPublishedAt);
            }
            if (postsByTags != null) {
                postsByFilter.addAll(postsByTags);
            }
        }
        for (Posts post : postsByFilter) {
            PostDTOForRest postDTOForRest = new PostDTOForRest();
            postDTOForRest.setTitle(post.getTitle());
            postDTOForRest.setAuthor(post.getAuthor());
            postDTOForRest.setContent(post.getContent());
            postDTOForRest.setPublishedAt(post.getPublishedAt());
            postDTOForRest.setTagsForPost(postsService.getTagsForPost(post.getPostId()));
            postDTOForRest.setCommentsForPost(commentsService.getCommentsForPost(post.getPostId()));
            allPostsDto.add(postDTOForRest);
        }
        return new ResponseEntity<>(allPostsDto, HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<List<PostDTOForRest>> getPostsBySearchPaginationAndSorting(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                                                                     @RequestParam(value = "field", required = false, defaultValue = "postId") String field,
                                                                                     @RequestParam(value = "searchWord", required = false) String searchWord) {
        int pageSize = 10;
        Page<Posts> postsBySearch = postsService.getPostsPaginationAndSorting(pageNumber, pageSize, field, searchWord);
        List<PostDTOForRest> postsBySearchPaginationAndSorting = new ArrayList<>();
        for (Posts post : postsBySearch) {
            PostDTOForRest postDTOForRest = new PostDTOForRest();
            postDTOForRest.setTitle(post.getTitle());
            postDTOForRest.setAuthor(post.getAuthor());
            postDTOForRest.setContent(post.getContent());
            postDTOForRest.setPublishedAt(post.getPublishedAt());
            postDTOForRest.setTagsForPost(postsService.getTagsForPost(post.getPostId()));
            postDTOForRest.setCommentsForPost(commentsService.getCommentsForPost(post.getPostId()));
            postsBySearchPaginationAndSorting.add(postDTOForRest);
        }
        return new ResponseEntity<>(postsBySearchPaginationAndSorting, HttpStatus.OK);
    }

    @PostMapping("/users/login")
    public ResponseEntity<String> validateTheUser(@RequestHeader("Authorization") String authorization) {
        if (commentsRestController.getHeaders(authorization)) {
            return new ResponseEntity<>("Successful Login", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid Credentials", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/savePost")
    public ResponseEntity<String> saveNewPost(@RequestBody PostsDto postsDto, @RequestHeader("Authorization") String authorization) {
        if (commentsRestController.getHeaders(authorization)) {
            Posts posts = new Posts();
            posts.setTagsList(tagsDto.saveTagsData(postsDto.getTags()));
            posts.setTitle(postsDto.getTitle());
            posts.setContent(postsDto.getContent());
            if (postsDto.getContent().length() < 255) {
                posts.setExcerpt(postsDto.getContent());
            } else {
                posts.setExcerpt(postsDto.getContent().substring(0, 254));
            }
            posts.setPublished(true);
            String[] authorDetails = restAPIService.base64ToString(authorization).split(":");
            User user = userService.findByEmail(authorDetails[0]);
            posts.setAuthor(user.getName());
            postsService.addPost(posts);
            return new ResponseEntity<>("Successfully Stored the post", HttpStatus.OK);
        }
        return new ResponseEntity<>("Un-Successful", HttpStatus.UNAUTHORIZED);
    }

    @PutMapping("/editPost")
    public ResponseEntity<String> editPost(@RequestParam("postId") int postId,
                                           @RequestBody PostsDto postsDto,
                                           @RequestHeader("Authorization") String authorization) {
        Posts previouesPost = postsService.getTheSpecificPost(postId);
        String[] authorDetails = restAPIService.base64ToString(authorization).split(":");
        User user = userService.findByEmail(authorDetails[0]);
        if (restAPIService.validateTheUserForPost(authorDetails[0], postId) && restAPIService.validateTheHeader(authorization)) {
            Posts posts = new Posts();
            posts.setPostId(postId);
            posts.setAuthor(user.getName());
            posts.setAuthor(postsDto.getTitle());
            posts.setContent(postsDto.getContent());
            posts.setPublished(previouesPost.isPublished());
            if (postsDto.getContent().length() < 255) {
                posts.setExcerpt(postsDto.getContent());
            } else {
                posts.setExcerpt(postsDto.getContent().substring(0, 254));
            }
            posts.setTagsList(tagsDto.saveTagsData(postsDto.getTags()));
            posts.setPublishedAt(previouesPost.getPublishedAt());
            posts.setCreatedAt(previouesPost.getCreatedAt());
            posts.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            postsService.addPost(posts);
            return new ResponseEntity<>("Successfully Updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("Un-Authorized Author", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<String> deletePost(@RequestHeader("Authorization") String authorization,
                                             @RequestParam("postId") int postId) {
        String[] authorDetails = restAPIService.base64ToString(authorization).split(":");
        if (restAPIService.validateTheUserForPost(authorDetails[0], postId) && restAPIService.validateTheHeader(authorization)) {
            try {
                postsService.deletePost(postId);
            } catch (Exception e) {
               return new ResponseEntity<>("The PostID you are searching to delete is not Found",HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Successfully Deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Un-Authorized Author", HttpStatus.UNAUTHORIZED);
    }
}
