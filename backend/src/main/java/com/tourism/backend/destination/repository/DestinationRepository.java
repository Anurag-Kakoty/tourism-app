package com.tourism.backend.destination.repository;

import com.tourism.backend.destination.entity.Destination;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {

    Optional<Destination> findByNameIgnoreCaseAndState_Id(
            String name,
            Long stateId
    );

    @EntityGraph(attributePaths = {"state", "tags", "experiences"})
    Optional<Destination> findWithStateById(Long id);

    @EntityGraph(attributePaths = {"state", "tags", "experiences"})
    List<Destination> findAllBy();

    @EntityGraph(attributePaths = {"state", "tags", "experiences"})
    List<Destination> findAllByState_NameIgnoreCase(String stateName);

}