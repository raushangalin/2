package com.example.controller;

import com.example.dto.UserDTO;
import com.example.entity.UserEntity;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API tests for UserController using MockMvc
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController API Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserEntity testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity(1L, "John Doe", "john@example.com", 30);
        testUserDTO = new UserDTO(1L, "John Doe", "john@example.com", 30);
    }

    @Test
    @DisplayName("POST /api/users - Should create user successfully")
    void testCreateUserSuccess() throws Exception {
        UserDTO createRequest = new UserDTO(null, "Jane Smith", "jane@example.com", 25);
        when(userService.createUser(any(UserEntity.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).createUser(any(UserEntity.class));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return user by ID")
    void testGetUserByIdSuccess() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("GET /api/users - Should return all users")
    void testGetAllUsersSuccess() throws Exception {
        UserEntity user2 = new UserEntity(2L, "Jane Smith", "jane@example.com", 25);
        List<UserEntity> users = Arrays.asList(testUser, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"))      // ← ИСПРАВЛЕНо
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));   // ← ИСПРАВЛЕНо

        verify(userService, times(1)).getAllUsers();
    }


    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user successfully")
    void testDeleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
}
