package com.mountblue.Repos;

import com.mountblue.model.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Set;

@Repository
@EnableJpaRepositories
public interface PostsRepository extends JpaRepository<Posts, Integer> {
    @Query(value = "Select Max(post_id) from posts", nativeQuery = true)
    int findMaxPostid();

//    @Query(value = "SELECT DISTINCT p FROM Posts p JOIN p.tagsList as t WHERE "+
//            "t.name ILIKE %:value% OR "+
//            "p.title ILIKE %:value% OR "+
//            "p.author ILIKE %:value% OR " +
//            "P.content ILIKE %:value%")
    @Query(value = "select * from posts where author @@ to_tsquery('english',:value) " +
            "or content @@ to_tsquery('english', :value)" +
            " or excerpt @@ to_tsquery('english',:value)" +
            " or title @@ to_tsquery('english',:value)" +
            " union " +
            "select * from posts where post_id IN " +
            "( select post_id from post_tags where tags_id IN" +
            " ( Select tag_id from tags where " +
            "name @@ to_tsquery('english',:value)))", nativeQuery = true)
    Page<Posts> findByText(@Param("value") String value, Pageable pageable);

    @Query(value = "select * from posts where author=:author", nativeQuery = true)
    Set<Posts> findByAuthor(String author);

    @Query(value = "select * from posts where published_at = :publishedAt ", nativeQuery = true)
    Set<Posts> findByPublishedAt(Timestamp publishedAt);

    @Query(value = "select * from posts where post_id in (select post_id from post_tags where tags_id in (Select tag_id from tags where name = :tag))", nativeQuery = true)
    Set<Posts> findByTags(String tag);
}
