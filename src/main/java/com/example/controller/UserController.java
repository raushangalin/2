package com.example.controller;

import com.example.dto.UserDTO;
import com.example.entity.UserEntity;
import com.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserEntity userEntity = new UserEntity(userDTO.getName(), userDTO.getEmail(), userDTO.getAge());
        UserEntity createdUser = userService.createUser(userEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(createdUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserEntity> user = userService.getUserById(id);

        return user.map(u -> ResponseEntity.ok(toDTO(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        Optional<UserEntity> user = userService.getUserByEmail(email);

        return user.map(u -> ResponseEntity.ok(toDTO(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserEntity userEntity = new UserEntity(id, userDTO.getName(), userDTO.getEmail(), userDTO.getAge());
        UserEntity updatedUser = userService.updateUser(userEntity);

        return ResponseEntity.ok(toDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    private UserDTO toDTO(UserEntity user) {

        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }
}
