package com.tourism.backend.user.repository;

import com.tourism.backend.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"interests"})
    Optional<User> findWithInterestsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"interests"})
    Optional<User> findWithInterestsById(Long id);

    boolean existsByEmailIgnoreCase(String email);
}
