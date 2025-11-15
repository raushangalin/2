package com.example.service;

import com.example.dto.UserDTO;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should create user successfully")
    public void testCreateUser() {
        UserDTO inputDTO = new UserDTO(null, "John", "john@example.com", 30);
        UserEntity savedEntity = new UserEntity(1L, "John", "john@example.com", 30);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        UserDTO result = userService.createUser(inputDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    public void testCreateUserWithDuplicateEmail() {
        UserDTO inputDTO = new UserDTO(null, "John", "john@example.com", 30);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(inputDTO));
    }

    @Test
    @DisplayName("Should get user by id")
    public void testGetUserById() {
        UserEntity entity = new UserEntity(1L, "John", "john@example.com", 30);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<UserDTO> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName());
    }

    @Test
    @DisplayName("Should get all users")
    public void testGetAllUsers() {
        UserEntity entity1 = new UserEntity(1L, "John", "john@example.com", 30);
        UserEntity entity2 = new UserEntity(2L, "Jane", "jane@example.com", 25);

        when(userRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getName());
        assertEquals("Jane", result.get(1).getName());
    }

    @Test
    @DisplayName("Should update user successfully")
    public void testUpdateUser() {
        UserEntity existingEntity = new UserEntity(1L, "John", "john@example.com", 30);
        UserDTO updateDTO = new UserDTO(null, "John Updated", "john@example.com", 31);
        UserEntity updatedEntity = new UserEntity(1L, "John Updated", "john@example.com", 31);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);

        UserDTO result = userService.updateUser(1L,updateDTO);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals(31, result.getAge());
    }

    @Test
    @DisplayName("Should delete user successfully")
    public void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if user exists by id")
    public void testUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.userExists(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    public void testUserExistsByEmail() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        boolean result = userService.userExistsByEmail("john@example.com");

        assertTrue(result);
    }
}
