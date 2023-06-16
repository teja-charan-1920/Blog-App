package com.mountblue.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.ContentHandler;
@Data
@AllArgsConstructor
public class PostsDto {
    private String title;
    private String tags;
    private String content;
}
