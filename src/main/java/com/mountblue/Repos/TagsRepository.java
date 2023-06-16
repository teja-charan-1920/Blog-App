package com.mountblue.Repos;

import com.mountblue.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TagsRepository extends JpaRepository<Tags, Integer> {
    @Query(value = "Delete from tags where tag_id not in (Select tags_id from post_tags)", nativeQuery = true)
    @Modifying
    void deleteTagsNotInPostTags();
}
