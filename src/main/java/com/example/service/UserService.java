package com.example.service;

import com.example.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing User entities.
 * Defines business logic operations for user management including CRUD operations.
 *
 * Implementations must handle:
 * - Input validation
 * - Exception handling
 * - Transaction management
 */
public interface UserService {

    /**
     * Creates a new user in the database.
     * Validates input and checks for duplicate emails.
     *
     * @param userEntity the user entity to create, must not be null
     * @return the created UserEntity with generated ID
     * @throws IllegalArgumentException if user data is invalid or email already exists
     * @throws IllegalArgumentException if email is empty or null
     * @throws IllegalArgumentException if name is empty or null
     */
    UserEntity createUser(UserEntity userEntity);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user, must be positive
     * @return Optional containing the user if found, empty Optional otherwise
     * @throws IllegalArgumentException if ID is null or invalid (less than or equal to 0)
     */
    Optional<UserEntity> getUserById(Long id);

    /**
     * Retrieves all users from the database.
     *
     * @return List of all users, empty list if no users exist
     */
    List<UserEntity> getAllUsers();

    /**
     * Retrieves a user by email address.
     * Email must be unique in the system.
     *
     * @param email the email address to search for, must not be null or empty
     * @return Optional containing the user if found, empty Optional otherwise
     * @throws IllegalArgumentException if email is null or empty
     */
    Optional<UserEntity> getUserByEmail(String email);

    /**
     * Updates an existing user with new data.
     * User must exist in the database before update.
     *
     * @param userEntity the user entity with updated data, must not be null
     * @return the updated UserEntity
     * @throws IllegalArgumentException if user entity is null
     * @throws IllegalArgumentException if user ID is null or invalid
     * @throws IllegalArgumentException if user does not exist in database
     */
    UserEntity updateUser(UserEntity userEntity);

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the unique identifier of the user to delete, must be positive
     * @throws IllegalArgumentException if ID is null or invalid
     * @throws IllegalArgumentException if user does not exist
     */
    void deleteUser(Long id);

    /**
     * Checks if a user exists by their unique identifier.
     *
     * @param id the user ID to check, must be positive
     * @return true if user with this ID exists, false otherwise
     */
    boolean userExists(Long id);

    /**
     * Checks if a user exists by email address.
     *
     * @param email the email address to check, must not be null or empty
     * @return true if user with this email exists, false otherwise
     */
    boolean userExistsByEmail(String email);
}
