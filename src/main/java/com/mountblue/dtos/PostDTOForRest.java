package com.mountblue.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTOForRest {
    private String title;
    private String author;
    private String content;
    private Timestamp publishedAt;
    private Set<String> tagsForPost;
    private List<CommentsDtoForRest> commentsForPost;
}
