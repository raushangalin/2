package com.example.controller;

import com.example.dto.UserDTO;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for UserController REST endpoints.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/users - Should return all users")
    public void testGetAllUsersSuccess() throws Exception {
        UserDTO user1 = new UserDTO(1L, "John", "john@example.com", 30);
        UserDTO user2 = new UserDTO(2L, "Jane", "jane@example.com", 25);
        List<UserDTO> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[1].name").value("Jane"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return user by id")
    public void testGetUserByIdSuccess() throws Exception {
        UserDTO user = new UserDTO(1L, "John", "john@example.com", 30);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/users - Should create new user")
    public void testCreateUserSuccess() throws Exception {
        UserDTO inputDTO = new UserDTO(null, "John", "john@example.com", 30);
        UserDTO savedDTO = new UserDTO(1L, "John", "john@example.com", 30);

        when(userService.createUser(any(UserDTO.class))).thenReturn(savedDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update user")
    public void testUpdateUserSuccess() throws Exception {
        UserDTO updateDTO = new UserDTO(null, "John Updated", "john@example.com", 31);
        UserDTO updatedDTO = new UserDTO(1L, "John Updated", "john@example.com", 31);

        when(userService.updateUser(1L, updateDTO)).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }




    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user")
    public void testDeleteUserSuccess() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
