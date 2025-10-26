package com.userservice.console;

import com.userservice.entity.UserEntity;
import com.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Консольный интерфейс для взаимодействия с пользователем
 */
public class ConsoleInterface {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleInterface.class);
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleInterface(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Запуск главного меню
     */
    public void start() {
        logger.info("Starting console interface");

        while (true) {
            try {
                displayMenu();
                int choice = readIntInput("Выберите действие: ");

                if (!handleMenuChoice(choice)) {
                    break;
                }
            } catch (Exception e) {
                logger.error("Error in console interface", e);
            }
        }
        logger.info("Console interface stopped");
    }

    /**
     * Отображение главного меню
     */
    private void displayMenu() {
        System.out.println("\n=== USER SERVICE MENU ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Найти пользователя по Email");
        System.out.println("4. Показать всех пользователей");
        System.out.println("5. Обновить пользователя");
        System.out.println("6. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.println("========================");
    }

    /**
     * Обработка выбора пользователя
     *
     * @return false если выбран выход
     */
    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                createUser();
                break;
            case 2:
                findUserById();
                break;
            case 3:
                findUserByEmail();
                break;
            case 4:
                showAllUsers();
                break;
            case 5:
                updateUser();
                break;
            case 6:
                deleteUser();
                break;
            case 0:
                System.out.println("Выход из приложения...");
                return false;
            default:
                System.out.println("Неверный выбор. Попробуйте снова.");
        }
        return true;
    }

    /**
     * Создание нового пользователя
     */
    private void createUser() {
        System.out.println("\n--- Создание пользователя ---");

        try {
            String name = readStringInput("Введите имя: ");
            String email = readStringInput("Введите email: ");
            Integer age = readOptionalIntInput("Введите возраст (Enter чтобы пропустить): ");

            UserEntity userEntity = userService.createUser(name, email, age);
            System.out.println("Пользователь создан: " + userEntity);

        } catch (IllegalArgumentException e) {
            logger.error("Error validation user", e);
        } catch (Exception e) {
            logger.error("Error creating user", e);
        }
    }

    /**
     * Поиск пользователя по ID
     */
    private void findUserById() {
        System.out.println("\n--- Поиск по ID ---");

        try {
            long id = readLongInput("Введите ID: ");
            Optional<UserEntity> user = userService.getUserById(id);

            if (user.isPresent()) {
                System.out.println("Найден пользователь: " + user.get());
            } else {
                System.out.println("Пользователь с ID " + id + " не найден");
            }

        } catch (Exception e) {
            logger.error("Error finding user by id", e);
        }
    }

    /**
     * Поиск пользователя по email
     */
    private void findUserByEmail() {
        System.out.println("\n--- Поиск по Email ---");

        try {
            String email = readStringInput("Введите email: ");
            Optional<UserEntity> user = userService.getUserByEmail(email);

            if (user.isPresent()) {
                System.out.println("Найден пользователь: " + user.get());
            } else {
                System.out.println("Пользователь с email " + email + " не найден");
            }

        } catch (Exception e) {
            logger.error("Error finding user by email", e);
        }
    }

    /**
     * Отображение всех пользователей
     */
    private void showAllUsers() {
        System.out.println("\n--- Все пользователи ---");

        try {
            List<UserEntity> userEntities = userService.getAllUsers();

            if (userEntities.isEmpty()) {
                System.out.println("Список пользователей пуст");
            } else {
                System.out.println("Найдено пользователей: " + userEntities.size());
                userEntities.forEach(System.out::println);
            }

        } catch (Exception e) {
            logger.error("Error getting all users", e);
        }
    }

    /**
     * Обновление пользователя
     */
    private void updateUser() {
        System.out.println("\n--- Обновление пользователя ---");

        try {
            long id = readLongInput("Введите ID пользователя: ");

            // Проверка существования
            Optional<UserEntity> existingUser = userService.getUserById(id);
            if (existingUser.isEmpty()) {
                System.out.println("Пользователь с ID " + id + " не найден");

                return;
            }

            System.out.println("Текущие данные: " + existingUser.get());

            String name = readStringInput("Введите новое имя: ");
            String email = readStringInput("Введите новый email: ");
            Integer age = readOptionalIntInput("Введите новый возраст (Enter чтобы пропустить): ");

            UserEntity updated = userService.updateUser(id, name, email, age);
            System.out.println("Пользователь обновлен: " + updated);

        } catch (IllegalArgumentException e) {
            logger.error("Error validation user", e);
        } catch (Exception e) {
            logger.error("Error updating user", e);
        }
    }

    /**
     * Удаление пользователя
     */
    private void deleteUser() {
        System.out.println("\n--- Удаление пользователя ---");

        try {
            long id = readLongInput("Введите ID пользователя: ");

            String confirmation = readStringInput("Вы уверены? (yes/no): ");
            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Удаление отменено");

                return;
            }

            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                System.out.println("Пользователь удален");
            } else {
                System.out.println("Пользователь с ID " + id + " не найден");
            }

        } catch (Exception e) {
            logger.error("Error deleting user", e);
        }
    }

    /**
     * Утилиты для чтения ввода
     */
    private String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число");
            }
        }
    }

    private long readLongInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                return Long.parseLong(input);

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число");
            }
        }
    }

    private Integer readOptionalIntInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {

            return null;
        }

        try {

            return Integer.parseInt(input);

        } catch (NumberFormatException e) {
            System.out.println("Предупреждение: некорректный формат, значение будет null");

            return null;
        }
    }
}
