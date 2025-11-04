package com.example.dao;

import com.example.entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import java.util.Optional;

/**
 * Реализация UserDao с поддержкой SessionFactory
 * Может работать как с обычной SessionFactory, так и с тестовой
 */
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);
    private final SessionFactory sessionFactory;

    /**
     * Конструктор с явной передачей SessionFactory
     * Используется в тестах и явном использовании
     */
    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Конструктор без параметров для совместимости с существующим кодом
     * Использует HibernateUtil.getSessionFactory()
     */
    public UserDaoImpl() {
        this(com.example.util.HibernateUtil.getSessionFactory());
    }

    @Override
    public UserEntity save(UserEntity user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved successfully: {}", user.getEmail());

            return user;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error saving user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    @Override
    public UserEntity update(UserEntity user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UserEntity updatedUser = session.merge(user);
            transaction.commit();
            logger.info("User updated successfully: {}", user.getEmail());

            return updatedUser;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        delete(user.getId());
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            UserEntity user = session.get(UserEntity.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("User deleted successfully, id: {}", id);
            } else {
                logger.warn("User not found for deletion, id: {}", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting user by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<UserEntity> query = session.createQuery("FROM com.example.entity.UserEntity", UserEntity.class);
            List<UserEntity> users = query.getResultList();
            logger.debug("Found {} users", users.size());

            return users;

        } catch (Exception e) {
            logger.error("Error finding all users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return findByField("id", id);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return findByField("email", email);
    }

    private Optional<UserEntity> findByField(String fieldName, Object fieldValue) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM com.example.entity.UserEntity WHERE " + fieldName + " = :fieldValue";
            Query<UserEntity> query = session.createQuery(hql, UserEntity.class);
            query.setParameter("fieldValue", fieldValue);
            UserEntity user = query.uniqueResult();
            if (user != null) {
                logger.debug("User found by {}: {}", fieldName, fieldValue);
            } else {
                logger.debug("User not found by {}: {}", fieldName, fieldValue);
            }

            return Optional.ofNullable(user);

        } catch (Exception e) {
            logger.error("Error finding user by {} {}: {}", fieldName, fieldValue, e.getMessage(), e);
            throw new RuntimeException("Failed to find user by " + fieldName, e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return existsByCondition("id = :id", "id", id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return existsByCondition("email = :email", "email", email);
    }

    private boolean existsByCondition(String condition, String parameterName, Object parameterValue) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(id) FROM UserEntity WHERE " + condition;
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter(parameterName, parameterValue);
            Long count = query.uniqueResult();

            return count != null && count > 0;

        } catch (Exception e) {
            logger.error("Error checking existence by {}: {}", parameterName, e.getMessage(), e);
            throw new RuntimeException("Failed to check existence", e);
        }
    }
}