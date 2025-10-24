package com.userservice;

import com.userservice.console.ConsoleInterface;
import com.userservice.dao.UserDAO;
import com.userservice.dao.UserDAOImpl;
import com.userservice.service.UserService;
import com.userservice.service.UserServiceImpl;
import com.userservice.util.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Главный класс приложения
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("=== USER SERVICE APPLICATION STARTING ===");

        try {
            // Инициализация компонентов
            logger.info("Initializing application components...");

            // Проверка доступности SessionFactory
            HibernateUtil.getSessionFactory();
            logger.info("Database connection established");

            // Создание зависимостей (manual dependency injection)
            UserDAO userDAO = new UserDAOImpl();
            UserService userService = new UserServiceImpl(userDAO);
            ConsoleInterface consoleInterface = new ConsoleInterface(userService);

            logger.info("Application components initialized successfully");

            // Запуск консольного интерфейса
            consoleInterface.start();
        } catch (Exception e) {
            logger.error("Fatal error during application startup", e);
            System.exit(1);
        } finally {
            shutdown();
        }
    }

    /**
     * Завершение работы приложения
     */
    private static void shutdown() {
        try {
            logger.info("=== SHUTTING DOWN APPLICATION ===");

            // Закрытие Hibernate SessionFactory
            if (HibernateUtil.isSessionFactoryAvailable()) {
                HibernateUtil.shutdown();
            }

            logger.info("=== APPLICATION SHUTDOWN COMPLETE ===");

        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
    }
}
