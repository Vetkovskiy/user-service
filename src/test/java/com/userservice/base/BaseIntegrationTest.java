package com.userservice.base;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Базовый класс для интеграционных тестов с использованием Testcontainers
 */
@Testcontainers
public abstract class BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseIntegrationTest.class);

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
     * Инициализация контейнера перед всеми тестами класса
     * Выполняется ОДИН раз
     */
    @BeforeAll
    static void initContainer() {
        logger.info("Starting PostgreSQL container...");
        // Контейнер запускается автоматически через @Container
        logger.info("PostgreSQL container started: {}", postgresContainer.getJdbcUrl());
    }

    /**
     * Создание SessionFactory перед КАЖДЫМ тестом
     * Обеспечивает изоляцию через create-drop
     */
    @BeforeEach
    void setUp() {
        logger.debug("Creating SessionFactory for test");
        sessionFactory = createSessionFactory();
        logger.debug("SessionFactory created");
    }

    /**
     * Закрытие SessionFactory после КАЖДОГО теста
     * Критично для предотвращения утечек ресурсов
     */
    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.debug("Closing SessionFactory");
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
            
            // КРИТИЧНО: Переопределение параметров подключения из контейнера
            configuration.setProperty("hibernate.connection.url", postgresContainer.getJdbcUrl());
            configuration.setProperty("hibernate.connection.username", postgresContainer.getUsername());
            configuration.setProperty("hibernate.connection.password", postgresContainer.getPassword());
            
            return configuration.buildSessionFactory();
            
        } catch (Exception e) {
            logger.error("Failed to create SessionFactory", e);
            throw new RuntimeException("Failed to create test SessionFactory", e);
        }
    }

    /**
     * Вспомогательный метод для очистки таблиц между тестами
     * Опционально - можно использовать если не хотите пересоздавать SessionFactory
     */
    protected void cleanDatabase() {
        sessionFactory.inTransaction(session ->
                session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE", Object.class)
            .executeUpdate());
    }

    /**
     * Получение SessionFactory для тестов
     */
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
