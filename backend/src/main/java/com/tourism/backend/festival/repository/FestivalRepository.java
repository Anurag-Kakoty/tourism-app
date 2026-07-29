package com.tourism.backend.festival.repository;

import com.tourism.backend.festival.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

    Optional<Festival> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}