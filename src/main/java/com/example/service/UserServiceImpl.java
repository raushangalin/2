package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.UserEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Optional;

/**
 * Реализация UserService
 * Содержит бизнес-логику и делегирует DAO-операции UserDao
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {

        // Валидация
        if (userEntity == null) {
            throw new IllegalArgumentException("User entity cannot be null");
        }
        if (userEntity.getEmail() == null || userEntity.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
        if (userEntity.getName() == null || userEntity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        logger.debug("Attempting to create user with email: {}", userEntity.getEmail());

        // Проверка уникальности email
        if (userDao.existsByEmail(userEntity.getEmail())) {
            throw new IllegalArgumentException("User with email " + userEntity.getEmail() + " already exists");
        }

        UserEntity saved = userDao.save(userEntity);
        logger.info("User created successfully with ID: {}", saved.getId());

        return saved;
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        logger.debug("Getting user by ID: {}", id);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userDao.findById(id);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        logger.debug("Getting all users");

        return userDao.findAll();
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        logger.debug("Getting user by email: {}", email);
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        return userDao.findByEmail(email);
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {

        if (userEntity == null) {
            throw new IllegalArgumentException("User entity cannot be null");
        }
        if (userEntity.getId() == null || userEntity.getId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID for update");
        }

        logger.debug("Attempting to update user with ID: {}", userEntity.getId());
        // Проверка существования пользователя
        if (!userDao.existsById(userEntity.getId())) {
            throw new IllegalArgumentException("User with ID " + userEntity.getId() + " not found");
        }

        UserEntity updated = userDao.update(userEntity);
        logger.info("User updated successfully with ID: {}", updated.getId());

        return updated;
    }

    @Override
    public void deleteUser(Long id) {
        logger.debug("Attempting to delete user with ID: {}", id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (!userDao.existsById(id)) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }

        userDao.delete(id);
        logger.info("User deleted successfully with ID: {}", id);
    }

    @Override
    public boolean userExists(Long id) {
        if (id == null || id <= 0) {

            return false;
        }

        return userDao.existsById(id);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {

            return false;
        }

        return userDao.existsByEmail(email);
    }
}