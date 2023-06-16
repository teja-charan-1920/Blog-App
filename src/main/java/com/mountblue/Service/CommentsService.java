package com.mountblue.Service;

import com.mountblue.Repos.CommentsRepository;
import com.mountblue.dtos.CommentsDtoForRest;
import com.mountblue.model.Comments;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data

public class CommentsService {
    private final CommentsRepository commentsRepository;

    public void saveCommentsData(Comments comments) {
        commentsRepository.save(comments);
    }

    public List<Comments> getCommentsByPostId(int id) {
        return commentsRepository.findByPostId(id);
    }

    public Comments getCommentById(int id) {
        return commentsRepository.findById(id).get();
    }

    public void deleteById(int id) {
        commentsRepository.deleteById(id);
    }

    public List<CommentsDtoForRest> getCommentsForPost(int postId) {
        List<Comments> commentsForPost = commentsRepository.findByPostId(postId);
        List<CommentsDtoForRest> commentsDtoForRests = new ArrayList<>();
        for(Comments comments : commentsForPost){
            CommentsDtoForRest commentsDto = new CommentsDtoForRest();
            commentsDto.setComment(comments.getComment());
            commentsDto.setName(comments.getName());
            commentsDto.setEmail(comments.getEmail());
            commentsDtoForRests.add(commentsDto);
        }
        return commentsDtoForRests;
    }
}
