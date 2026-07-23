package com.tourism.backend.repository;

import com.tourism.backend.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {

    Optional<State> findByName(String name);

    boolean existsByName(String name);
}