package com.example.dao;

import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private static final Logger logger = LogManager.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User updatedUser = session.merge(user);
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
    public void delete(User user) {
        delete(user.getId());
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
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
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM com.example.entity.User", User.class);
            List<User> users = query.getResultList();
            logger.debug("Found {} users", users.size());

            return users;

        } catch (Exception e) {
            logger.error("Error finding all users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return findByField("id", id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findByField("email", email);
    }

    private Optional<User> findByField(String fieldName, Object fieldValue) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM com.example.entity.User WHERE " + fieldName + " = :fieldValue";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("fieldValue", fieldValue);

            User user = query.uniqueResult();

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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(id) FROM User WHERE " + condition;
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