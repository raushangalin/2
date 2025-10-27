package com.example;

import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class BetterTest {
    public static void main(String[] args) {
        System.out.println("=== УЛУЧШЕННЫЙ ТЕСТ ===");

        try {
            // Проверим SessionFactory
            if (HibernateUtil.getSessionFactory() == null) {
                System.out.println("❌ SessionFactory не создан");
                return;
            }
            System.out.println("✅ SessionFactory создан");

            Session session = HibernateUtil.getSessionFactory().openSession();
            System.out.println("✅ Сессия открыта");

            // Проверим метаданные
            var entityPersister = session.getSessionFactory().getMetamodel().entity(User.class);
            System.out.println("✅ Сущность User найдена в метаданных: " + entityPersister);

            Transaction transaction = session.beginTransaction();
            System.out.println("✅ Транзакция начата");

            // Создаем пользователя
            User user = new User("Test User", "test@test.com", 25);
            System.out.println("🔄 Создаем пользователя: " + user);

            session.persist(user);
            transaction.commit();

            System.out.println("✅ Пользователь создан в БД: " + user);

            session.close();
            System.out.println("🎉 ТЕСТ ПРОЙДЕН УСПЕШНО!");

        } catch (Exception e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}