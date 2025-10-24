package com.userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton класс для управления Hibernate SessionFactory
 */
public class HibernateUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    /**
     * Получение экземпляра SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                logger.info("Initializing Hibernate SessionFactory");

                Configuration configuration = new Configuration();
                configuration.configure();
                
                sessionFactory = configuration.buildSessionFactory();
                
                logger.info("SessionFactory initialized successfully");
            } catch (Exception e) {
                logger.error("Error creating SessionFactory", e);
                throw e;
            }
        }

        return sessionFactory;
    }

    /**
     * Закрытие SessionFactory
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Closing SessionFactory");
            sessionFactory.close();
            logger.info("SessionFactory closed");
        }
    }

    /**
     * Проверка доступности SessionFactory
     */
    public static boolean isSessionFactoryAvailable() {
        return sessionFactory != null && !sessionFactory.isClosed();
    }
}
