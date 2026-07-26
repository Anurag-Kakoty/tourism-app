package com.tourism.backend.tag.mapper;

import com.tourism.backend.tag.dto.TagRequest;
import com.tourism.backend.tag.dto.TagResponse;
import com.tourism.backend.tag.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public Tag toEntity(TagRequest request) {

        Tag tag = new Tag();
        updateEntity(tag, request);

        return tag;
    }

    public void updateEntity(Tag tag, TagRequest request) {

        tag.setName(request.getName());
        tag.setDescription(request.getDescription());
    }

    public TagResponse toResponse(Tag tag) {

        TagResponse response = new TagResponse();

        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setDescription(tag.getDescription());

        return response;
    }
}