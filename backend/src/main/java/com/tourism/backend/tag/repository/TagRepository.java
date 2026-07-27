package com.tourism.backend.tag.repository;

import com.tourism.backend.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNameIgnoreCase(String name);

    List<Tag> findByIdIn(Iterable<Long> ids);

}