package com.mountblue.Repos;

import com.mountblue.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Integer> {
    @Query(value = "Select * from comments where post_id=:postId", nativeQuery = true)
    List<Comments> findByPostId(int postId); // id should be capital
}
