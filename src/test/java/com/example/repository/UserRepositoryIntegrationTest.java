package com.example.repository;

import com.example.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository with Spring Data JPA
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("John Doe", saved.getName());
        assertEquals("john@example.com", saved.getEmail());
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        UserEntity user = new UserEntity("Jane Smith", "jane@example.com", 25);
        userRepository.save(user);

        Optional<UserEntity> found = userRepository.findByEmail("jane@example.com");

        assertTrue(found.isPresent());
        assertEquals("Jane Smith", found.get().getName());
    }

    @Test
    @DisplayName("Should return false for non-existent email")
    void testFindByEmailNotFound() {
        Optional<UserEntity> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void testExistsByEmail() {
        UserEntity user = new UserEntity("Bob Johnson", "bob@example.com", 35);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("bob@example.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false if user does not exist by email")
    void testExistsByEmailNotFound() {
        boolean exists = userRepository.existsByEmail("notexist@example.com");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete user by ID")
    void testDeleteUser() {
        UserEntity user = new UserEntity("Tom Brown", "tom@example.com", 40);
        UserEntity saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        Optional<UserEntity> found = userRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}
