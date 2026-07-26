package com.tourism.backend.destination.repository;

import com.tourism.backend.destination.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
}