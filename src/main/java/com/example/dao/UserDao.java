package com.example.dao;

import com.example.entity.UserEntity;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for UserEntity
 * Provides CRUD operations and additional queries
 */
public interface UserDao {

    /**
     * Saves a new user to the database
     * @param user the user to save
     * @return the saved user with generated ID
     */
    UserEntity save(UserEntity user);

    /**
     * Finds a user by ID
     * @param id the user ID
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<UserEntity> findById(Long id);

    /**
     * Retrieves all users from the database
     * @return list of all users
     */
    List<UserEntity> findAll();

    /**
     * Finds a user by email address
     * @param email the email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Updates an existing user
     * @param user the user with updated data
     * @return the updated user
     */
    UserEntity update(UserEntity user);

    /**
     * Deletes a user by entity
     * @param user the user to delete
     */
    void delete(UserEntity user);

    /**
     * Deletes a user by ID
     * @param id the ID of user to delete
     */
    void delete(Long id);

    /**
     * Checks if a user exists with the given ID
     * @param id the user ID to check
     * @return true if user exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Checks if a user exists with the given email
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
}