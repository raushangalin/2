package com.example.service;

import com.example.entity.UserEntity;

import java.util.List;

import java.util.Optional;

/**
 * UserService - business logic for user operations
 * Provides high-level operations on UserEntity
 */

public interface UserService {

    /**
     * Creates a new user
     * @param userEntity user to create
     * @return created user with ID
     */
    UserEntity createUser(UserEntity userEntity);

    /**
     * Gets a user by ID
     * @param id user ID
     * @return Optional with user or empty Optional
     */
    Optional<UserEntity> getUserById(Long id);

    /**
     * Gets all users
     * @return list of all users
     */
    List<UserEntity> getAllUsers();

    /**
     * Gets a user by email
     * @param email user email address
     * @return Optional with user or empty Optional
     */
    Optional<UserEntity> getUserByEmail(String email);

    /**
     * Updates an existing user
     * @param userEntity user with updated data
     * @return updated user
     * @throws IllegalArgumentException if user not found
     */
    UserEntity updateUser(UserEntity userEntity);

    /**
     * Deletes a user by ID
     * @param id user ID to delete
     */
    void deleteUser(Long id);

    /**
     * Checks if a user exists by ID
     * @param id ID to check
     * @return true if exists, false otherwise
     */
    boolean userExists(Long id);

    /**
     * Checks if a user exists by email
     * @param email email to check
     * @return true if exists, false otherwise
     */
    boolean userExistsByEmail(String email);
}