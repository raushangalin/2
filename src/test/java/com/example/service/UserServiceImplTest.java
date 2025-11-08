package com.example.service;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity(1L, "John Doe", "john@example.com", 30);
    }

    // ============= CREATE TESTS =============

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser() {
        UserEntity newUser = new UserEntity("John Doe", "john@example.com", 30);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        UserEntity created = userService.createUser(newUser);

        assertNotNull(created);
        assertEquals(1L, created.getId());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating user with null entity")
    void testCreateUserWithNullEntity() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));
    }

    // ============= READ TESTS =============

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<UserEntity> found = userService.getUserById(1L);

        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getId());
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers() {
        List<UserEntity> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ============= UPDATE TESTS =============

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        UserEntity userToUpdate = new UserEntity(1L, "John Updated", "john@example.com", 31);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any())).thenReturn(userToUpdate);

        UserEntity updated = userService.updateUser(userToUpdate);

        assertEquals("John Updated", updated.getName());
    }

    // ============= DELETE TESTS =============

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);  // ✅ ПРАВИЛЬНО!

    }

    // ============= EXISTS TESTS =============

    @Test
    @DisplayName("Should return true when user exists by ID")
    void testUserExistsById() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean exists = userService.userExists(1L);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void testUserExistsByEmail() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        boolean exists = userService.userExistsByEmail("john@example.com");

        assertTrue(exists);
    }
}
