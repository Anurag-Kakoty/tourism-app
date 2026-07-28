package com.tourism.backend.experience.repository;

import com.tourism.backend.experience.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    Optional<Experience> findByNameIgnoreCase(String name);

}