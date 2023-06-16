package com.mountblue.Service;

import com.mountblue.Repos.TagsRepository;
import com.mountblue.model.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class TagsService {
    private final TagsRepository tagsRepository;

    public void deleteTagsNotInPostTags() {
         tagsRepository.deleteTagsNotInPostTags();
    }

    public void deleteTags(Tags tags){
        tagsRepository.delete(tags);
    }
}
