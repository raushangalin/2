package com.example.service;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing users with Spring Data JPA
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        if (userEntity == null) {
            throw new IllegalArgumentException("User entity cannot be null");
        }

        if (userEntity.getEmail() == null || userEntity.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be empty");
        }

        if (userEntity.getName() == null || userEntity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }

        if (userRepository.existsByEmail(userEntity.getEmail())) {
            throw new IllegalArgumentException("User with email " + userEntity.getEmail() + " already exists");
        }

        UserEntity savedUser = userRepository.save(userEntity);
        logger.info("User created successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }

        return userRepository.findById(id);
    }

    @Override
    public List<UserEntity> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        if (userEntity == null) {
            throw new IllegalArgumentException("User entity cannot be null");
        }

        if (userEntity.getId() == null || userEntity.getId() <= 0) {
            throw new IllegalArgumentException("User ID must be valid for update");
        }

        if (!userRepository.existsById(userEntity.getId())) {
            throw new IllegalArgumentException("User with ID " + userEntity.getId() + " does not exist");
        }

        UserEntity updatedUser = userRepository.save(userEntity);
        logger.info("User updated successfully with ID: {}", updatedUser.getId());

        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("User ID must be valid");
        }

        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with ID " + id + " does not exist");
        }

        userRepository.deleteById(id);
        logger.info("User deleted successfully with ID: {}", id);
    }

    @Override
    public boolean userExists(Long id) {
        if (id == null || id <= 0) {

            return false;
        }

        return userRepository.existsById(id);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {

            return false;
        }

        return userRepository.existsByEmail(email);
    }
}
