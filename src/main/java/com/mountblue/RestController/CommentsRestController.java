package com.mountblue.RestController;

import com.mountblue.Service.CommentsService;
import com.mountblue.Service.PostsService;
import com.mountblue.Service.RestAPIService;
import com.mountblue.dtos.CommentsDtoForRest;
import com.mountblue.model.Comments;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blog")
@Data
@AllArgsConstructor
public class CommentsRestController {
    private final CommentsService commentsService;
    private final PostsService postsService;
    private final RestAPIService restAPIService;

    @PostMapping("/saveComment")
    public ResponseEntity<String> saveComment(@RequestBody CommentsDtoForRest commentsDtoForRest, @RequestParam("postId") int postId) {
        Comments comments = new Comments();
        comments.setComment(commentsDtoForRest.getComment());
        comments.setName(commentsDtoForRest.getName());
        comments.setEmail(commentsDtoForRest.getEmail());
        comments.setPostId(postId);
        commentsService.saveCommentsData(comments);
        return new ResponseEntity<>("Comment saved Successfully :)", HttpStatus.OK);
    }

    public boolean getHeaders(@RequestHeader("Authorization") String authorization) {
        if (restAPIService.validateTheHeader(authorization)) {
            return true;
        } else {
            return false;
        }
    }

    @PutMapping("/editComment")
    public ResponseEntity<String> editPost(@RequestHeader("Authorization") String authorization,
                                           @RequestParam("postId") int postId,
                                           @RequestBody CommentsDtoForRest commentsDtoForRest,
                                           @RequestParam("id") int id) {
        if (getHeaders(authorization)) {
            Comments comment = commentsService.getCommentById(id);
            if (comment.getPostId() != postId) {
                return new ResponseEntity<>("This Comment is Not Belongs to the Post", HttpStatus.UNAUTHORIZED);
            }
            String[] authorDetails = restAPIService.base64ToString(authorization).split(":");
            if (restAPIService.validateTheUserForPost(authorDetails[0], postId)) {
                Comments comments = new Comments();
                comments.setId(id);
                comments.setName(commentsDtoForRest.getName());
                comments.setEmail(commentsDtoForRest.getEmail());
                comments.setComment(commentsDtoForRest.getComment());
                comments.setPostId(comment.getPostId());
                comments.setCreatedAt(comment.getCreatedAt());
                commentsService.saveCommentsData(comments);
                return new ResponseEntity<>("Comment Updated Successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Un- Authorized", HttpStatus.UNAUTHORIZED);
    }

    @DeleteMapping("/deleteComment")
    public ResponseEntity<String> deleteComment(@RequestHeader("Authorization") String authorization,
                                                @RequestParam("postId") int postId,
                                                @RequestParam("id") int id) {
        if (restAPIService.validateTheHeader(authorization)) {
            String[] authorDetails = restAPIService.base64ToString(authorization).split(":");
            if (restAPIService.validateTheUserForPost(authorDetails[0], postId)) {
                Comments comment = commentsService.getCommentById(id);
                if (comment.getPostId() != postId) {
                    return new ResponseEntity<>("This Comment is Not Belongs to the Post", HttpStatus.UNAUTHORIZED);
                }
                commentsService.deleteById(id);
                return new ResponseEntity<>("Deleted comment Successfully", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Un Authorized user", HttpStatus.UNAUTHORIZED);
    }
}
