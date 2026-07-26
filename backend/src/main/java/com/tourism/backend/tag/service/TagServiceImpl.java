package com.tourism.backend.tag.service;

import com.tourism.backend.exception.DuplicateResourceException;
import com.tourism.backend.exception.ResourceNotFoundException;
import com.tourism.backend.tag.dto.TagRequest;
import com.tourism.backend.tag.dto.TagResponse;
import com.tourism.backend.tag.entity.Tag;
import com.tourism.backend.tag.mapper.TagMapper;
import com.tourism.backend.tag.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private static final Logger logger =
            LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository,
                          TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public TagResponse createTag(TagRequest request) {

        logger.info("Creating tag '{}'", request.getName());

        tagRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {

                    logger.warn("Duplicate tag '{}' attempted",
                            request.getName());

                    throw new DuplicateResourceException(
                            "Tag '" + request.getName()
                                    + "' already exists."
                    );
                });

        Tag tag = tagMapper.toEntity(request);

        Tag savedTag = tagRepository.save(tag);

        logger.info("Tag '{}' created successfully with id {}",
                savedTag.getName(),
                savedTag.getId());

        return tagMapper.toResponse(savedTag);
    }

    @Override
    public List<TagResponse> getAllTags() {

        logger.info("Fetching all tags");

        return tagRepository.findAll()
                .stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Override
    public TagResponse getTagById(Long id) {

        logger.info("Fetching tag with id {}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Tag with id {} not found", id);

                    return new ResourceNotFoundException(
                            "Tag with id " + id + " not found."
                    );
                });

        return tagMapper.toResponse(tag);
    }

    @Override
    public TagResponse updateTag(Long id,
                                 TagRequest request) {

        logger.info("Updating tag with id {}", id);

        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Tag with id {} not found", id);

                    return new ResourceNotFoundException(
                            "Tag with id " + id + " not found."
                    );
                });

        tagRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {

                    if (!existing.getId().equals(id)) {

                        logger.warn(
                                "Duplicate tag '{}' attempted during update",
                                request.getName());

                        throw new DuplicateResourceException(
                                "Tag '" + request.getName()
                                        + "' already exists."
                        );
                    }
                });

        tagMapper.updateEntity(tag, request);

        Tag updatedTag = tagRepository.save(tag);

        logger.info("Tag '{}' updated successfully",
                updatedTag.getName());

        return tagMapper.toResponse(updatedTag);
    }

    @Override
    public void deleteTag(Long id) {

        logger.info("Deleting tag with id {}", id);

        if (!tagRepository.existsById(id)) {

            logger.warn("Delete failed. Tag with id {} not found", id);

            throw new ResourceNotFoundException(
                    "Tag with id " + id + " not found."
            );
        }

        tagRepository.deleteById(id);

        logger.info("Tag with id {} deleted successfully", id);
    }
}