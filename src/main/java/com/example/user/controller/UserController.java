package com.example.user.controller;

import com.example.user.dto.UserCreateRequest;
import com.example.user.entity.UserEntity;
import com.example.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Конструктор с внедрением зависимости.
     *
     * @param userService сервис пользователей
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создать нового пользователя.
     *
     * @param request DTO с данными пользователя
     * @return созданный пользователь
     */
    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserCreateRequest request) {
        logger.info("Creating user: username={}, email={}", request.getUsername(), request.getEmail());

        if (request == null || request.getEmail() == null || request.getUsername() == null) {
            logger.error("Invalid request: required fields are missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            UserEntity userEntity = new UserEntity(request.getUsername(), request.getEmail(), request.getPassword());
            UserEntity createdUser = userService.createUser(userEntity);
            logger.info("User created successfully: id={}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException ex) {
            logger.error("Validation error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex) {
            logger.error("Error creating user", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить пользователя по ID.
     *
     * @param id ID пользователя
     * @return пользователь
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        logger.info("Getting user by ID: {}", id);

        try {
            UserEntity user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException ex) {
            logger.error("User not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.error("Error getting user", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить пользователя по email.
     *
     * @param email адрес электронной почты
     * @return пользователь
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email) {
        logger.info("Getting user by email: {}", email);

        try {
            UserEntity user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException ex) {
            logger.error("User not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.error("Error getting user", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Обновить пользователя.
     *
     * @param id ID пользователя
     * @param request DTO с данными пользователя
     * @return обновленный пользователь
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserCreateRequest request) {
        logger.info("Updating user: id={}", id);

        try {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(id);
            userEntity.setUsername(request.getUsername());
            userEntity.setEmail(request.getEmail());
            userEntity.setPassword(request.getPassword());

            UserEntity updatedUser = userService.updateUser(userEntity);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException ex) {
            logger.error("Validation error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex) {
            logger.error("Error updating user", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Удалить пользователя.
     *
     * @param id ID пользователя
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user: id={}", id);

        try {
            userService.deleteUser(id);
            logger.info("User deleted successfully: id={}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            logger.error("User not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.error("Error deleting user", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}