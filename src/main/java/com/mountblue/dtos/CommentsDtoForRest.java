package com.mountblue.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentsDtoForRest {
    private String name;
    private String email;
    private String comment;
}
