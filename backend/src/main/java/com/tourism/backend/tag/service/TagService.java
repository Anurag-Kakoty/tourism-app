package com.tourism.backend.tag.service;

import com.tourism.backend.tag.dto.TagRequest;
import com.tourism.backend.tag.dto.TagResponse;

import java.util.List;

public interface TagService {

    TagResponse createTag(TagRequest request);

    List<TagResponse> getAllTags();

    TagResponse getTagById(Long id);

    TagResponse updateTag(Long id, TagRequest request);

    void deleteTag(Long id);

}