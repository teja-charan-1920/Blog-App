package com.mountblue.Controllers;

import com.mountblue.DTO.PostDto;
import com.mountblue.DTO.TagsDto;
import com.mountblue.Repos.PostsRepository;
import com.mountblue.Service.CommentsService;
import com.mountblue.Service.PostsService;
import com.mountblue.Service.TagsService;
import com.mountblue.Service.UserService;
import com.mountblue.model.Comments;
import com.mountblue.model.Posts;
import com.mountblue.model.Tags;
import com.mountblue.model.User;
import com.mountblue.Service.SecurityService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@Data
@AllArgsConstructor
@SuppressWarnings("all")
public class PostsController {
    private final PostsRepository postsRepository;
    private final PostsService postsService;
    private final TagsService tagsService;
    private final PostDto postDto;
    private final TagsDto tagsDto;
    private final CommentsService commentsService;
    private final UserService userService;
    private final SecurityService securityService;

    @RequestMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("post", new Posts());
        return postsPagination(model, 0, "postId", null);
    }

    @RequestMapping("/post")
    public ModelAndView getSpecificPost(@RequestParam("Id") int id,@RequestParam(value = "email",required = false) String email) {
        User user = null;
        if(email != null){
            user = userService.findByEmail(email);
        }
        ModelAndView modelAndView = new ModelAndView("post");
        Posts post = postsService.getTheSpecificPost(id);
        List<Comments> commentsForPost = commentsService.getCommentsByPostId(id);
        modelAndView.addObject("user", user);
        modelAndView.addObject("post", post);
        modelAndView.addObject("comments", commentsForPost);
        return modelAndView;
    }

    @RequestMapping("/new-post")
    public ModelAndView newPost() {
        ModelAndView modelAndView = new ModelAndView("new-post");
        Posts posts = new Posts();
        modelAndView.addObject("isRequestEdit", false);
        modelAndView.addObject("posts", posts);
        return modelAndView;
    }

    @PostMapping("/savepost")
    public String savePostData(@ModelAttribute("posts") Posts post, @RequestParam("tags") String tags, @RequestParam(value = "isEditRequest", required = false) boolean isEditRequest) {
        List<Tags> postTags = null;
        if (isEditRequest) {
            postTags = tagsDto.editPostTags(post.getTagsList(), tags);
        } else {
            postTags = tagsDto.saveTagsData(tags);
        }
        Posts postToSave = postDto.savePostData(post, postTags);
        postsService.addPost(postToSave);
        return "redirect:/";
    }

    @RequestMapping("/editPost")
    public ModelAndView editPost(@RequestParam("id") int postId) {
        ModelAndView modelAndView = new ModelAndView();
        if (securityService.validateThePostOwner(postId)) {
            modelAndView.setViewName("new-post");
            Posts post = postsService.getTheSpecificPost(postId);
            String tags = postDto.getPostTagsData(postId);
            modelAndView.addObject("PublishedAt", post.getPublishedAt());
            modelAndView.addObject("CreatedAt", post.getCreatedAt());
            modelAndView.addObject("isRequestEdit", true);
            modelAndView.addObject("tags", tags);
            modelAndView.addObject("posts", post);
            return modelAndView;
        } else {
            modelAndView.setViewName("error-display");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/{pageNumber}/{pageSize}")
    public ModelAndView postsPagination(@PathVariable int pageNumber, @PathVariable int pageSize) {
        ModelAndView modelAndView = new ModelAndView("home-page");
        Page<Posts> postsByPage = postsService.getPostsPagination(pageNumber, pageSize);
        modelAndView.addObject("isPagination", true);
        modelAndView.addObject("totalPages", postsByPage.getTotalPages());
        modelAndView.addObject("pageNo", pageNumber);
        modelAndView.addObject("currentSize", pageSize);
        modelAndView.addObject("posts", postsByPage);
        return modelAndView;
    }

    //@PreAuthorize("(@securityService.validateThePostOwner(#id) AND hasAuthority('AUTHOR')) OR hasAuthority('ADMIN')")
    @RequestMapping("/deletePost")
    public String deleteSpecificPost(@RequestParam("id") int postId) {
        if (securityService.validateThePostOwner(postId)) {
            try {
                postsService.deletePost(postId);
            } catch (Exception e) {

            }
            try {
                tagsService.deleteTagsNotInPostTags();
            } catch (Exception e) {
            }
            return "redirect:/";
        }
        return "error-display";
    }

    @RequestMapping("/page")
    public String postsPagination(Model model, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                                  @RequestParam(value = "field", required = false, defaultValue = "postId") String field,
                                  @RequestParam(value = "searchWord", required = false) String searchWord) {
        int pageSize = 10;
        Page<Posts> postsByPage = postsService.getPostsPaginationAndSorting(pageNumber, pageSize, field, searchWord);
        if (postsByPage.isEmpty()) {
            return "redirect:/";
        }
        List<Posts> allPosts = postsService.getAllPosts();
        Set<String> authors = new HashSet<>();
        Set<String> tags = new HashSet<>();
        for (Posts post : allPosts) {
            authors.add(post.getAuthor());
            for (Tags tag : post.getTagsList()) {
                tags.add(tag.getName());
            }
        }
        model.addAttribute("allPostData", allPosts);
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("field", field);
        model.addAttribute("totalPages", postsByPage.getTotalPages());
        model.addAttribute("pageNo", pageNumber);
        model.addAttribute("totalItems", postsByPage.getTotalElements());
        model.addAttribute("currentSize", pageSize);
        model.addAttribute("posts", postsByPage);
        return "home-page";
    }

    @RequestMapping("/filter")
    public String postFilter(Model model, @RequestParam(value = "filter1", required = false) String author,
                             @RequestParam(value = "filter2", required = false) String publishedAt,
                             @RequestParam(value = "filter3", required = false) String tag) {
        Set<Posts> postsByAuthor = null;
        Set<Posts> postsByPublishedAt = null;
        Set<Posts> postsByTags = null;
        List<Posts> allPosts = postsService.getAllPosts();
        Set<String> authors = new HashSet<>();
        Set<String> tags = new HashSet<>();
        for (Posts post : allPosts) {
            authors.add(post.getAuthor());
            for (Tags tagsInPost : post.getTagsList()) {
                tags.add(tagsInPost.getName());
            }
        }
        model.addAttribute("allPostData", allPosts);
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
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
            return "redirect:/";
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
        model.addAttribute("posts", postsByFilter);
        return "home-page";
    }
}
