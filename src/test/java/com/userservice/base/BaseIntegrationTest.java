package com.userservice.base;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Базовый класс для Testcontainers
 */
@Testcontainers
public class BaseIntegrationTest {

    /**
     * PostgreSQL контейнер - создается ОДИН раз для всех тестов класса
     */
    @SuppressWarnings("resource")
    @Container
    protected static final PostgreSQLContainer<?> postgresContainer = 
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false);  // false для изоляции между запусками

    protected static SessionFactory sessionFactory;

    /**
     * Создание SessionFactory перед КАЖДЫМ тестом
     */
    @BeforeEach
    void setUp() {
        sessionFactory = createSessionFactory();
    }

    /**
     * Закрытие SessionFactory после каждого теста
     */
    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    /**
     * Создание SessionFactory с параметрами из Testcontainers
     */
    private SessionFactory createSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            
            // Загрузка базовой конфигурации из hibernate-test.cfg.xml
            configuration.configure("hibernate-test.cfg.xml");
            
            //Переопределение параметров подключения из контейнера
            configuration.setProperty("hibernate.hikari.dataSource.url", postgresContainer.getJdbcUrl());
            configuration.setProperty("hibernate.hikari.dataSource.user", postgresContainer.getUsername());
            configuration.setProperty("hibernate.hikari.dataSource.password", postgresContainer.getPassword());
            
            return configuration.buildSessionFactory();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test SessionFactory", e);
        }
    }

    /**
     * Получение SessionFactory для тестов
     */
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
