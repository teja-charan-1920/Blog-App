package com.mountblue.Controllers;

import com.mountblue.Service.CommentsService;
import com.mountblue.model.Comments;
import com.mountblue.Service.SecurityService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Data
@AllArgsConstructor
@SuppressWarnings("all")
public class CommentsController {
    private final CommentsService commentsService;
    private final SecurityService securityService;

    @PostMapping("/addcomment")
    public String addComment(@ModelAttribute("comments") Comments comment,
                             @RequestParam("post_id") int postId,
                             @RequestParam(value = "gmail",required = false) String email) {
        Comments comments = new Comments();
        comments.setId(comment.getId());
        comments.setComment(comment.getComment());
        comments.setName(comment.getName());
        comments.setCreatedAt(comment.getCreatedAt());
        comments.setPostId(postId);
        comments.setEmail(comment.getEmail());
        commentsService.saveCommentsData(comments);
        if(email == null){
            return "redirect:/post?Id=" + postId;
        }
        return "redirect:/post?Id=" + postId+"&email="+email;
    }

    @RequestMapping("/typeComment")
    public ModelAndView typeComment(@RequestParam("Id") int id) {
        ModelAndView modelAndView = new ModelAndView("comments");
        Comments comments = new Comments();
        modelAndView.addObject("isRequestEdit", false);
        modelAndView.addObject("post_id", id);
        modelAndView.addObject("comments", comments);
        return modelAndView;
    }

    @PostMapping("/editComment")
    public ModelAndView editComment(@RequestParam("comment-id") int id, @RequestParam("post-id") int postId) {
        ModelAndView modelAndView = new ModelAndView();
        if (securityService.validateThePostOwner(postId)) {
            modelAndView.setViewName("comments");
            Comments comments = commentsService.getCommentById(id);
            modelAndView.addObject("isRequestEdit", true);
            modelAndView.addObject("CreatedAt", comments.getCreatedAt());
            modelAndView.addObject("comments", comments);
            modelAndView.addObject("post_id", postId);
            return modelAndView;
        } else {
            modelAndView.setViewName("error-display");
            return modelAndView;
        }

    }

    @PostMapping("/deleteComment")
    public String deleteComment(@RequestParam("comment-id") int id,
                                @RequestParam("post-id") int postId,
                                @RequestParam(value = "gmail",required = false) String email) {
        if (securityService.validateThePostOwner(postId)) {
            commentsService.deleteById(id);
            return "redirect:/post?Id=" + postId+"&email="+email;
        }
        return "error-display";
    }
}
