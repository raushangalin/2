package com.example.repository;

import com.example.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Integration Tests")
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user successfully")
    public void testSaveUser() {
        UserEntity user = new UserEntity("John", "john@example.com", 30);

        UserEntity savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("John", savedUser.getName());
    }

    @Test
    @DisplayName("Should find user by id")
    public void testFindById() {
        UserEntity user = new UserEntity("John", "john@example.com", 30);
        UserEntity savedUser = userRepository.save(user);

        Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getName());
    }

    @Test
    @DisplayName("Should find all users")
    public void testFindAll() {
        userRepository.save(new UserEntity("John", "john@example.com", 30));
        userRepository.save(new UserEntity("Jane", "jane@example.com", 25));

        List<UserEntity> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Should find user by email")
    public void testFindByEmail() {
        userRepository.save(new UserEntity("John", "john@example.com", 30));

        Optional<UserEntity> foundUser = userRepository.findByEmail("john@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("John", foundUser.get().getName());
    }

    @Test
    @DisplayName("Should check if user exists by email")
    public void testExistsByEmail() {
        userRepository.save(new UserEntity("John", "john@example.com", 30));

        boolean exists = userRepository.existsByEmail("john@example.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should delete user successfully")
    public void testDeleteUser() {
        UserEntity user = new UserEntity("John", "john@example.com", 30);
        UserEntity savedUser = userRepository.save(user);

        userRepository.deleteById(savedUser.getId());

        Optional<UserEntity> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }
}
