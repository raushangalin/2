package com.example.user.service;

import com.example.user.dto.UserEventDto;
import com.example.user.entity.UserEntity;
import com.example.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса для управления пользователями с интеграцией Kafka.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String KAFKA_TOPIC = "user-events";

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param userRepository репозиторий пользователей
     * @param kafkaTemplate шаблон для отправки сообщений в Kafka
     */
    public UserServiceImpl(UserRepository userRepository, KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        if (userEntity == null || userEntity.getEmail() == null || userEntity.getEmail().isEmpty()) {
            logger.error("Invalid user data: userEntity is null or email is empty");
            throw new IllegalArgumentException("UserEntity and email cannot be null or empty");
        }

        if (userRepository.existsByEmail(userEntity.getEmail())) {
            logger.warn("User with email already exists: {}", userEntity.getEmail());
            throw new IllegalArgumentException("User with this email already exists");
        }

        UserEntity createdUser = userRepository.save(userEntity);
        logger.info("User created successfully: id={}, email={}", createdUser.getId(), createdUser.getEmail());

        sendUserEvent("CREATE", createdUser.getEmail());

        return createdUser;
    }

    @Override
    public UserEntity getUserById(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid user ID: {}", id);
            throw new IllegalArgumentException("User ID must be greater than 0");
        }

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new IllegalArgumentException("User not found with ID: " + id);
                });
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            logger.error("Invalid email: {}", email);
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        return user;
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        if (userEntity == null || userEntity.getId() == null) {
            logger.error("Invalid user data for update: userEntity is null or ID is null");
            throw new IllegalArgumentException("UserEntity and user ID cannot be null");
        }

        UserEntity existingUser = getUserById(userEntity.getId());
        logger.info("Updating user: id={}", userEntity.getId());

        if (userEntity.getUsername() != null && !userEntity.getUsername().isEmpty()) {
            existingUser.setUsername(userEntity.getUsername());
        }

        if (userEntity.getEmail() != null && !userEntity.getEmail().isEmpty()) {
            existingUser.setEmail(userEntity.getEmail());
        }

        if (userEntity.getPassword() != null && !userEntity.getPassword().isEmpty()) {
            existingUser.setPassword(userEntity.getPassword());
        }

        UserEntity updatedUser = userRepository.save(existingUser);
        logger.info("User updated successfully: id={}", updatedUser.getId());

        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            logger.error("Invalid user ID for deletion: {}", id);
            throw new IllegalArgumentException("User ID must be greater than 0");
        }

        UserEntity user = getUserById(id);
        String email = user.getEmail();

        userRepository.deleteById(id);
        logger.info("User deleted successfully: id={}, email={}", id, email);

        sendUserEvent("DELETE", email);
    }

    @Override
    public boolean userExists(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }

    /**
     * Отправить событие пользователя в Kafka.
     *
     * @param operation тип операции (CREATE или DELETE)
     * @param email адрес электронной почты пользователя
     */
    private void sendUserEvent(String operation, String email) {
        try {
            UserEventDto event = new UserEventDto(operation, email);
            kafkaTemplate.send(KAFKA_TOPIC, event);
            logger.info("User event sent to Kafka: operation={}, email={}, topic={}", operation, email, KAFKA_TOPIC);
        } catch (Exception ex) {
            logger.error("Failed to send user event to Kafka: operation={}, email={}", operation, email, ex);
        }
    }
}