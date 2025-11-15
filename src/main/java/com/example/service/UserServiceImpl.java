package com.example.service;

import com.example.dto.UserDTO;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserService interface.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO == null || userDTO.getName() == null || userDTO.getEmail() == null) {
            throw new IllegalArgumentException("User data cannot be null");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDTO.getEmail() + " already exists");
        }

        UserEntity entity = toEntity(userDTO);
        UserEntity savedEntity = userRepository.save(entity);
        logger.info("User created with ID: {}", savedEntity.getId());

        return toDTO(savedEntity);
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        return userRepository.findByEmail(email).map(this::toDTO);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        if (id == null || userDTO == null) {
            throw new IllegalArgumentException("User ID and data cannot be null");
        }

        UserEntity existingEntity = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        existingEntity.setName(userDTO.getName());
        existingEntity.setEmail(userDTO.getEmail());
        existingEntity.setAge(userDTO.getAge());

        UserEntity updatedEntity = userRepository.save(existingEntity);
        logger.info("User updated with ID: {}", id);

        return toDTO(updatedEntity);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || !userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        logger.info("User deleted with ID: {}", id);
    }

    @Override
    public boolean userExists(Long id) {
        return id != null && userRepository.existsById(id);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        return email != null && userRepository.existsByEmail(email);
    }

    private UserDTO toDTO(UserEntity entity) {
        return new UserDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getAge()
        );
    }

    private UserEntity toEntity(UserDTO dto) {
        return new UserEntity(
                dto.getName(),
                dto.getEmail(),
                dto.getAge()
        );
    }
}
