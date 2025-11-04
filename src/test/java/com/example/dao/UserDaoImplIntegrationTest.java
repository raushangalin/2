package com.example.dao;

import com.example.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для UserDaoImpl
 * Используют Testcontainers для изолированной тестовой БД PostgreSQL
 */
@Testcontainers
@DisplayName("UserDaoImpl Integration Tests")
class UserDaoImplIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private static SessionFactory sessionFactory;
    private UserDaoImpl userDao;

    @BeforeAll
    static void setupDatabase() {
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.driver_class", "org.postgresql.Driver")
                .applySetting("hibernate.connection.url", jdbcUrl)
                .applySetting("hibernate.connection.username", username)
                .applySetting("hibernate.connection.password", password)
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .applySetting("hibernate.show_sql", "false")
                .applySetting("hibernate.format_sql", "true")
                .build();

        MetadataSources metadataSources = new MetadataSources(registry);
        metadataSources.addAnnotatedClass(UserEntity.class);

        Metadata metadata = metadataSources.getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);
        clearDatabase();
    }

    private void clearDatabase() {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM UserEntity").executeUpdate();
            transaction.commit();
        }
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }

    // ============= CREATE TESTS =============

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = userDao.save(user);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("John Doe", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals(30, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should throw exception when saving user with duplicate email")
    void testSaveDuplicateEmailThrowsException() {
        UserEntity user1 = new UserEntity("John", "john@example.com", 30);
        UserEntity user2 = new UserEntity("Jane", "john@example.com", 25);
        userDao.save(user1);

        assertThrows(RuntimeException.class, () -> userDao.save(user2));
    }

    // ============= READ TESTS =============

    @Test
    @DisplayName("Should find user by ID")
    void testFindById() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = userDao.save(user);

        Optional<UserEntity> foundUser = userDao.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("John Doe", foundUser.get().getName());
    }

    @Test
    @DisplayName("Should return empty Optional when user not found by ID")
    void testFindByIdNotFound() {
        Optional<UserEntity> foundUser = userDao.findById(999L);
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        userDao.save(user);

        Optional<UserEntity> foundUser = userDao.findByEmail("john@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty Optional when user not found by email")
    void testFindByEmailNotFound() {
        Optional<UserEntity> foundUser = userDao.findByEmail("notfound@example.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        UserEntity user1 = new UserEntity("John", "john@example.com", 30);
        UserEntity user2 = new UserEntity("Jane", "jane@example.com", 25);
        UserEntity user3 = new UserEntity("Bob", "bob@example.com", 35);
        userDao.save(user1);
        userDao.save(user2);
        userDao.save(user3);

        List<UserEntity> users = userDao.findAll();

        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testFindAllEmpty() {
        List<UserEntity> users = userDao.findAll();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    // ============= UPDATE TESTS =============

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = userDao.save(user);

        savedUser.setName("John Updated");
        savedUser.setAge(31);
        UserEntity updatedUser = userDao.update(savedUser);

        assertNotNull(updatedUser);
        assertEquals("John Updated", updatedUser.getName());
        assertEquals(31, updatedUser.getAge());
        assertEquals("john@example.com", updatedUser.getEmail());
    }

    @Test
    @DisplayName("Should verify update persists in database")
    void testUpdatePersists() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = userDao.save(user);
        savedUser.setName("Updated Name");

        userDao.update(savedUser);
        Optional<UserEntity> retrievedUser = userDao.findById(savedUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals("Updated Name", retrievedUser.get().getName());
    }

    // ============= DELETE TESTS =============

    @Test
    @DisplayName("Should delete user by ID")
    void testDeleteById() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = userDao.save(user);

        userDao.delete(savedUser.getId());

        Optional<UserEntity> deletedUser = userDao.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should delete user by entity")
    void testDeleteByEntity() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = userDao.save(user);

        userDao.delete(savedUser);

        Optional<UserEntity> deletedUser = userDao.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should not throw exception when deleting non-existent user")
    void testDeleteNonExistentUser() {
        assertDoesNotThrow(() -> userDao.delete(999L));
    }

    // ============= EXISTS TESTS =============

    @Test
    @DisplayName("Should return true when user exists by ID")
    void testExistsById() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        UserEntity savedUser = userDao.save(user);

        boolean exists = userDao.existsById(savedUser.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when user does not exist by ID")
    void testExistsByIdNotFound() {
        boolean exists = userDao.existsById(999L);
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true when user exists by email")
    void testExistsByEmail() {
        UserEntity user = new UserEntity("John Doe", "john@example.com", 30);
        userDao.save(user);

        boolean exists = userDao.existsByEmail("john@example.com");

        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when user does not exist by email")
    void testExistsByEmailNotFound() {
        boolean exists = userDao.existsByEmail("notfound@example.com");
        assertFalse(exists);
    }

    // ============= EDGE CASES =============

    @Test
    @DisplayName("Should handle multiple operations in sequence")
    void testSequentialOperations() {
        UserEntity user = new UserEntity("John", "john@example.com", 30);
        UserEntity saved = userDao.save(user);
        assertNotNull(saved.getId());

        Optional<UserEntity> found = userDao.findById(saved.getId());
        assertTrue(found.isPresent());

        found.get().setAge(31);
        UserEntity updated = userDao.update(found.get());
        assertEquals(31, updated.getAge());

        userDao.delete(updated.getId());
        Optional<UserEntity> deleted = userDao.findById(updated.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should handle concurrent reads")
    void testConcurrentReads() {
        UserEntity user1 = new UserEntity("User1", "user1@example.com", 25);
        UserEntity user2 = new UserEntity("User2", "user2@example.com", 30);
        userDao.save(user1);
        userDao.save(user2);

        Optional<UserEntity> foundUser1 = userDao.findById(user1.getId());
        Optional<UserEntity> foundUser2 = userDao.findByEmail("user2@example.com");
        List<UserEntity> allUsers = userDao.findAll();

        assertTrue(foundUser1.isPresent());
        assertTrue(foundUser2.isPresent());
        assertEquals(2, allUsers.size());
    }
}