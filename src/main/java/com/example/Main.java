package com.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.example.dao.UserDao;
import com.example.dao.UserDaoImpl;
import com.example.entity.UserEntity;
import com.example.service.UserService;
import com.example.service.UserServiceImpl;
import com.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main приложение User Service
 * Использует трехслойную архитектуру: Controller -> Service -> DAO
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final UserService userService = new UserServiceImpl(userDao);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== ЗАПУСК USER SERVICE ===");
        try {
            SessionFactory factory = HibernateUtil.getSessionFactory();
            if (factory == null) {
                System.out.println("Не удалось подключиться к базе данных.");
                System.out.println("Приложение будет закрыто.");

                return;
            }

            try (Session session = factory.openSession()) {
                System.out.println("✅ Подключение к базе данных успешно!");
            } catch (Exception e) {
                System.out.println("❌ Ошибка подключения к базе данных: " + e.getMessage());
                System.out.println("Проверь:");
                System.out.println("1. Запущен ли PostgreSQL?");
                System.out.println("2. Создана ли база данных 'user_db'?");
                System.out.println("3. Правильный ли пароль в настройках?");

                return;
            }

            logger.info("Starting User Service application");
            try {
                showMenu();
            } catch (Exception e) {
                logger.error("Application error: {}", e.getMessage(), e);
                System.err.println("Critical error: " + e.getMessage());
            } finally {
                HibernateUtil.shutdown();
                scanner.close();
                logger.info("User Service application stopped");
            }

        } catch (Exception e) {
            System.err.println("Ошибка инициализации приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMenu() {
        while (true) {
            System.out.println("\n=== User Service ===");
            System.out.println("1. Create User");
            System.out.println("2. Find User by ID");
            System.out.println("3. Find All Users");
            System.out.println("4. Find User by Email");
            System.out.println("5. Update User");
            System.out.println("6. Delete User");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        findUserById();
                        break;
                    case 3:
                        findAllUsers();
                        break;
                    case 4:
                        findUserByEmail();
                        break;
                    case 5:
                        updateUser();
                        break;
                    case 6:
                        deleteUser();
                        break;
                    case 0:
                        System.out.println("Goodbye!");

                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                logger.error("Menu operation error: {}", e.getMessage(), e);
            }
        }
    }

    private static void createUser() {
        System.out.println("\n--- Create User ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        try {
            UserEntity user = new UserEntity(name, email, age);
            UserEntity savedUser = userService.createUser(user);
            System.out.println("✅ User created successfully: " + savedUser);
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

    private static void findUserById() {
        System.out.println("\n--- Find User by ID ---");
        System.out.print("Enter user ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Optional<UserEntity> user = userService.getUserById(id);
            if (user.isPresent()) {
                System.out.println("✅ User found: " + user.get());
            } else {
                System.out.println("❌ User not found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format");
        }
    }

    private static void findAllUsers() {
        System.out.println("\n--- All Users ---");
        List<UserEntity> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void findUserByEmail() {
        System.out.println("\n--- Find User by Email ---");
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        Optional<UserEntity> user = userService.getUserByEmail(email);
        if (user.isPresent()) {
            System.out.println("✅ User found: " + user.get());
        } else {
            System.out.println("❌ User not found with email: " + email);
        }
    }

    private static void updateUser() {
        System.out.println("\n--- Update User ---");
        System.out.print("Enter user ID to update: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Optional<UserEntity> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                System.out.println("❌ User not found with ID: " + id);

                return;
            }

            UserEntity user = userOpt.get();
            System.out.println("Current user: " + user);
            System.out.print("Enter new name (current: " + user.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.trim().isEmpty()) {
                user.setName(name);
            }

            System.out.print("Enter new email (current: " + user.getEmail() + "): ");
            String email = scanner.nextLine();
            if (!email.trim().isEmpty()) {
                user.setEmail(email);
            }

            System.out.print("Enter new age (current: " + user.getAge() + "): ");
            String ageInput = scanner.nextLine();
            if (!ageInput.trim().isEmpty()) {
                user.setAge(Integer.parseInt(ageInput));
            }

            UserEntity updatedUser = userService.updateUser(user);
            System.out.println("✅ User updated successfully: " + updatedUser);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.println("\n--- Delete User ---");
        System.out.print("Enter user ID to delete: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            userService.deleteUser(id);
            System.out.println("✅ User deleted successfully");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
    }
}