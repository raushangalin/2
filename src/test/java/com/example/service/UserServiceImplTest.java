package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Юнит-тесты для UserServiceImpl
 * Используют Mockito для изоляции бизнес-логики от DAO-слоя
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

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
        when(userDao.existsByEmail("john@example.com")).thenReturn(false);
        when(userDao.save(any(UserEntity.class))).thenReturn(testUser);

        UserEntity created = userService.createUser(newUser);

        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("John Doe", created.getName());
        assertEquals("john@example.com", created.getEmail());
        assertEquals(30, created.getAge());

        verify(userDao, times(1)).existsByEmail("john@example.com");
        verify(userDao, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with null entity")
    void testCreateUserWithNullEntity() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));
        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating user with empty email")
    void testCreateUserWithEmptyEmail() {
        UserEntity user = new UserEntity("John Doe", "", 30);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating user with empty name")
    void testCreateUserWithEmptyName() {
        UserEntity user = new UserEntity("", "john@example.com", 30);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    void testCreateUserWithDuplicateEmail() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        when(userDao.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userDao, times(1)).existsByEmail("john@example.com");
        verify(userDao, never()).save(any());
    }

    // ============= READ TESTS =============

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<UserEntity> found = userService.getUserById(1L);

        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getId());
        assertEquals(testUser.getName(), found.get().getName());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty Optional when user not found by ID")
    void testGetUserByIdNotFound() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        Optional<UserEntity> found = userService.getUserById(999L);

        assertFalse(found.isPresent());
        verify(userDao, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception when getting user with invalid ID")
    void testGetUserByIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(null));
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(0L));
        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(-1L));
        verify(userDao, never()).findById(any());
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void testGetUserByEmail() {
        when(userDao.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        Optional<UserEntity> found = userService.getUserByEmail("john@example.com");

        assertTrue(found.isPresent());
        assertEquals("john@example.com", found.get().getEmail());
        verify(userDao, times(1)).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("Should return empty Optional when user not found by email")
    void testGetUserByEmailNotFound() {
        when(userDao.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<UserEntity> found = userService.getUserByEmail("notfound@example.com");

        assertFalse(found.isPresent());
        verify(userDao, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should throw exception when getting user with empty email")
    void testGetUserByEmailEmpty() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUserByEmail(""));
        assertThrows(IllegalArgumentException.class, () -> userService.getUserByEmail(null));
        verify(userDao, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers() {
        UserEntity user1 = new UserEntity(1L, "John", "john@example.com", 30);
        UserEntity user2 = new UserEntity(2L, "Jane", "jane@example.com", 25);
        UserEntity user3 = new UserEntity(3L, "Bob", "bob@example.com", 35);
        List<UserEntity> users = Arrays.asList(user1, user2, user3);
        when(userDao.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("John", result.get(0).getName());
        assertEquals("Jane", result.get(1).getName());
        assertEquals("Bob", result.get(2).getName());
        verify(userDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmpty() {
        when(userDao.findAll()).thenReturn(Arrays.asList());

        List<UserEntity> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findAll();
    }

    // ============= UPDATE TESTS =============

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        UserEntity userToUpdate = new UserEntity(1L, "John Updated", "john@example.com", 31);
        when(userDao.existsById(1L)).thenReturn(true);
        when(userDao.update(any(UserEntity.class))).thenReturn(userToUpdate);

        UserEntity updated = userService.updateUser(userToUpdate);

        assertNotNull(updated);
        assertEquals("John Updated", updated.getName());
        assertEquals(31, updated.getAge());
        verify(userDao, times(1)).existsById(1L);
        verify(userDao, times(1)).update(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with null entity")
    void testUpdateUserWithNullEntity() {
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null));
        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Should throw exception when updating user with invalid ID")
    void testUpdateUserWithInvalidId() {
        UserEntity user = new UserEntity();
        user.setId(null);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(user));
        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateNonExistentUser() {
        UserEntity user = new UserEntity(999L, "John", "john@example.com", 30);
        when(userDao.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(user));
        verify(userDao, times(1)).existsById(999L);
        verify(userDao, never()).update(any());
    }

    // ============= DELETE TESTS =============

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        when(userDao.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userDao, times(1)).existsById(1L);
        verify(userDao, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void testDeleteNonExistentUser() {
        when(userDao.existsById(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(999L));
        verify(userDao, times(1)).existsById(999L);
        verify(userDao, never()).delete(any(Long.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting user with invalid ID")
    void testDeleteUserWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(null));
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(0L));
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(-1L));
        verify(userDao, never()).delete(any(Long.class));
    }

    // ============= EXISTS TESTS =============

    @Test
    @DisplayName("Should return true when user exists by ID")
    void testUserExistsById() {
        when(userDao.existsById(1L)).thenReturn(true);

        boolean exists = userService.userExists(1L);

        assertTrue(exists);
        verify(userDao, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Should return false when user does not exist by ID")
    void testUserDoesNotExistById() {
        when(userDao.existsById(999L)).thenReturn(false);

        boolean exists = userService.userExists(999L);

        assertFalse(exists);
        verify(userDao, times(1)).existsById(999L);
    }

    @Test
    @DisplayName("Should return false for invalid ID in userExists")
    void testUserExistsByIdInvalid() {
        boolean exists1 = userService.userExists(null);
        boolean exists2 = userService.userExists(0L);

        assertFalse(exists1);
        assertFalse(exists2);
        verify(userDao, never()).existsById(any());
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void testUserExistsByEmail() {
        when(userDao.existsByEmail("john@example.com")).thenReturn(true);

        boolean exists = userService.userExistsByEmail("john@example.com");

        assertTrue(exists);
        verify(userDao, times(1)).existsByEmail("john@example.com");
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void testUserDoesNotExistByEmail() {
        when(userDao.existsByEmail("notfound@example.com")).thenReturn(false);

        boolean exists = userService.userExistsByEmail("notfound@example.com");

        assertFalse(exists);
        verify(userDao, times(1)).existsByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should return false for empty email in userExistsByEmail")
    void testUserExistsByEmailInvalid() {
        boolean exists1 = userService.userExistsByEmail(null);
        boolean exists2 = userService.userExistsByEmail("");

        assertFalse(exists1);
        assertFalse(exists2);
        verify(userDao, never()).existsByEmail(anyString());
    }

    // ============= INTEGRATION-LIKE TESTS =============

    @Test
    @DisplayName("Should handle complete CRUD workflow with mocks")
    void testCompleteCrudWorkflow() {
        UserEntity newUser = new UserEntity("John", "john@example.com", 30);
        when(userDao.existsByEmail("john@example.com")).thenReturn(false);
        when(userDao.save(any())).thenReturn(testUser);
        UserEntity created = userService.createUser(newUser);
        assertNotNull(created.getId());

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        Optional<UserEntity> read = userService.getUserById(1L);
        assertTrue(read.isPresent());

        UserEntity updated = new UserEntity(1L, "John Updated", "john@example.com", 31);
        when(userDao.existsById(1L)).thenReturn(true);
        when(userDao.update(any())).thenReturn(updated);
        UserEntity result = userService.updateUser(updated);
        assertEquals("John Updated", result.getName());

        userService.deleteUser(1L);
        verify(userDao, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should verify no unwanted DAO calls occur")
    void testVerifyNoUnwantedCalls() {
        UserEntity user = new UserEntity(1L, "John", "john@example.com", 30);

        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        userService.getUserById(1L);

        verify(userDao, times(1)).findById(1L);
        verify(userDao, never()).findAll();
        verify(userDao, never()).save(any());
        verify(userDao, never()).update(any());
        verify(userDao, never()).delete(any(Long.class));
    }
}