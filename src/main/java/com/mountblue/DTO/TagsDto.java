package com.mountblue.DTO;

import com.mountblue.Service.TagsService;
import com.mountblue.model.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.util.*;

@Controller
@Data
@AllArgsConstructor
public class TagsDto {
    private final TagsService tagsService;

    public List<Tags> saveTagsData(String tagData) {
        List<Tags> tagsList = new ArrayList<>();
        String[] tags = tagData.split(",");
        Set<String> originalTags = new HashSet<>(Arrays.asList(tags));
        for (String tagInPost : originalTags) {
            Tags tag = new Tags();
            tag.setName(tagInPost);
            Date date = new Date(System.currentTimeMillis());
            tag.setUpdatedAt(date);
            tagsList.add(tag);
        }
        return tagsList;
    }

    public List<Tags> editPostTags(List<Tags> postTags, String postUpdatedTags) {
        String editTags[] = postUpdatedTags.split(",");
        Set<String> withoutDuplicateTags = new HashSet<>(Arrays.asList(editTags));
        List<Tags> tagsAfterEdit = new ArrayList<>();
        Set<Tags> tagsToDelete = new HashSet<>();

        if (postTags != null) {
            for (Tags tags : postTags) {
                if (withoutDuplicateTags.contains(tags.getName())) {
                    withoutDuplicateTags.remove(tags.getName());
                    tags.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    tagsAfterEdit.add(tags);
                } else {
                    tagsToDelete.add(tags);
                }
            }
        }

        if (withoutDuplicateTags != null) {
            String tagsToString = "";
            for (String tag : withoutDuplicateTags) {
                tagsToString += tag + ",";
            }
            List<Tags> newTagsToPost = saveTagsData(tagsToString);
            for (Tags tags : newTagsToPost) {
                tagsAfterEdit.add(tags);
            }

        }

        if (tagsToDelete != null) {
            for (Tags tags : tagsToDelete) {
                try {
                    tagsService.deleteTags(tags);
                } catch (Exception exception) {
                }
            }
        }

        return tagsAfterEdit;
    }
}
