package com.example.repository;

import com.example.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User entity.
 * Provides database access layer for user operations.
 *
 * Extends JpaRepository to inherit CRUD methods:
 * - save(S entity): Save or update
 * - findAll(): Get all users
 * - findById(ID id): Get user by ID
 * - delete(T entity): Delete user
 * - deleteById(ID id): Delete by ID
 * - exists(ID id): Check if exists
 *
 * Custom methods for specific queries:
 * - findByEmail(String email): Find by email
 * - existsByEmail(String email): Check by email
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds a user by email address.
     * Email is unique across all users.
     *
     * @param email the email address to search for, must not be null
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check, must not be null
     * @return true if user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);
}
