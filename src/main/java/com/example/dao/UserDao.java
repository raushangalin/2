package com.example.dao;

import com.example.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    // Create
    User save(User user);

    // Read
    Optional<User> findById(Long id);
    List<User> findAll();
    Optional<User> findByEmail(String email);

    // Update
    User update(User user);

    // Delete
    void delete(Long id);
    void delete(User user);

    boolean existsById(Long id);
    boolean existsByEmail(String email);
}
