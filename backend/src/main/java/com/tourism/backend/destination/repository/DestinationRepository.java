package com.tourism.backend.destination.repository;

import com.tourism.backend.destination.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    List<Destination> findByState_NameIgnoreCase(String stateName);
}