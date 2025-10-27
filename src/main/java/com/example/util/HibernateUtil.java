package com.example.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import com.example.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    static {
        try {
            System.out.println("Пытаюсь подключиться к базе данных...");

            StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml")
                    .build();

            try {
                MetadataSources metadataSources = new MetadataSources(standardRegistry);
                metadataSources.addAnnotatedClass(User.class); 
                Metadata metadata = metadataSources.getMetadataBuilder().build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();

                System.out.println("Подключение к базе данных успешно!");
                System.out.println("Сущность User зарегистрирована в Hibernate!");

            } catch (Exception e) {
                System.err.println("Ошибка при создании SessionFactory: " + e.getMessage());
                e.printStackTrace();
                StandardServiceRegistryBuilder.destroy(standardRegistry);
                throw e;
            }

        } catch (Exception e) {
            System.err.println("ОШИБКА подключения к базе данных!");
            System.err.println("Сообщение: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory не инициализирован");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
