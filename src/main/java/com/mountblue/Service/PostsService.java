package com.mountblue.Service;

import com.mountblue.Repos.PostsRepository;
import com.mountblue.model.Posts;
import com.mountblue.model.Tags;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.type.descriptor.jdbc.TimestampWithTimeZoneJdbcType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Data
@AllArgsConstructor
public class PostsService {
    PostsRepository postsRepository;

    public Posts getTheSpecificPost(int id) {
        Posts post = postsRepository.findById(id).get();
        return post;
    }

    public void saveThePostDataInTable(Posts posts) {
        postsRepository.save(posts);
    }

    private final EntityManager entityManager;

//    public int getLastPostId() {
//        int i = postsRepository.findMaxPostid();
//        return i;
//    }

    public List<Posts> getAllPosts() {
        return postsRepository.findAll();
    }

    public void addPost(Posts posts) {
        postsRepository.save(posts);
    }

    public Page<Posts> getPostsPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return postsRepository.findAll(pageable);
    }

    public void deletePost(int id) throws Exception {
        postsRepository.deleteById(id);
    }

    public List<Posts> getPostsSorting(String property) {
        Sort sort = Sort.by(Sort.Direction.ASC, property);
        List<Posts> sortedPosts = postsRepository.findAll(sort);
        return sortedPosts;
    }

    public Page<Posts> getPostsPaginationAndSorting(int pageNumber, int pageSize, String field, String searchWord) {
        Sort sort = Sort.by(Sort.Direction.ASC, field);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Pageable pageableWithoutSort = PageRequest.of(pageNumber, pageSize);
        Page<Posts> sortedAndPaginationPosts = null;
        if (searchWord == null || searchWord.equals("")) {
            if (field != null) {
                sortedAndPaginationPosts = postsRepository.findAll(pageable);
            } else {
                sortedAndPaginationPosts = postsRepository.findAll(pageableWithoutSort);
            }

        } else {

            if (field != null) {
                Sort sortByModifyingFieldName = null;
                if(field.equals("postId")){
                     sortByModifyingFieldName = Sort.by(Sort.Direction.ASC,"post_id");
                } else if (field.equals("publishedAt")) {
                    sortByModifyingFieldName = Sort.by(Sort.Direction.ASC,"published_at");
                } else {
                    sortByModifyingFieldName = Sort.by(Sort.Direction.ASC,field);
                }

                Pageable pageable1 = PageRequest.of(pageNumber,pageSize,sortByModifyingFieldName);
                sortedAndPaginationPosts = postsRepository.findByText(searchWord,pageable1);
            }

        }
        return sortedAndPaginationPosts;
    }

    public Set<Posts> getPostsByAuthor(String author) {
        return postsRepository.findByAuthor(author);
    }

    public Set<Posts> getPostsByPublishedAt(String publishedAt) {
        Timestamp timestamp = Timestamp.valueOf(publishedAt);

        return postsRepository.findByPublishedAt(timestamp);
    }

    public Set<Posts> getPostsByTags(String tag) {
        return postsRepository.findByTags(tag);
    }

    public Set<String> getTagsForPost(int postId) {
        Posts posts = postsRepository.findById(postId).get();
        Set<String> postTags = new HashSet<>();
        for(Tags tags : posts.getTagsList()){
            postTags.add(tags.getName());
        }
        return postTags;
    }

    public Posts getPostById(int postId) throws Exception {
        return postsRepository.findById(postId).get();
    }
}
