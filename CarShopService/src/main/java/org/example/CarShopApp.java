package org.example;
import org.example.config.DatabaseConnectionManager;
import org.example.service.CarService;
import org.example.service.OrderService;
import org.example.service.UserService;
import org.example.logi.AuditLog;
import org.example.logi.AuditLogService;
import org.example.model.*;
import org.example.repository.CarRepository;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;


import java.util.List;
import java.util.Scanner;

import java.sql.SQLException;


public class CarShopApp {

    private static final String LINE_SEPARATOR = "----------------------------";
    private static final String HIGHLIGHT = "\033[1;34m";
    private static final String RESET = "\033[0m";
    private static final String SEPARATOR = "----------------------------";
    private static final String SUCCESS = "\033[0;32m";
    private static final String ERROR = "\033[0;31m";

    public static void main(String[] args) {
        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager();

        CarRepository carRepository = new CarRepository(dbConnectionManager);
        UserRepository userRepository = new UserRepository(dbConnectionManager);
        OrderRepository orderRepository = new OrderRepository(carRepository, userRepository, dbConnectionManager);

        CarService carService = new CarService(carRepository);
        UserService userService = new UserService(userRepository);
        OrderService orderService = new OrderService(orderRepository, userService, carRepository);
        AuditLogService auditLogService = new AuditLogService();

        Scanner scanner = new Scanner(System.in);
        String username = "";

        while (true) {
            showAuthMenu();
            int authChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (authChoice) {
                    case 1:
                        // Authentication
                        username = authenticateUser(userService, scanner);
                        if (username == null) {
                            System.out.println(ERROR + "Аутентификация не удалась. Попробуйте снова." + RESET);
                        } else {
                            System.out.println(SUCCESS + "Аутентификация успешна. Добро пожаловать, " + username + "!" + RESET);
                            mainMenu(scanner, carService, userService, orderService, auditLogService, username);
                            return; // Exit the authentication loop
                        }
                        break;
                    case 2:
                        // Registration
                        registerNewUser(userService, scanner);
                        break;
                    case 0:
                        System.out.println("Выход из программы.");
                        return;
                    default:
                        System.out.println(ERROR + "Неверный выбор. Попробуйте снова." + RESET);
                }
            } catch (SQLException e) {
                System.out.println(ERROR + "Ошибка базы данных: " + e.getMessage() + RESET);
            }
        }
    }

    private static void showAuthMenu() {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Меню аутентификации" + RESET);
        System.out.println(SEPARATOR);
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("0. Выход");
        System.out.println(SEPARATOR);
        System.out.print("Выберите действие: ");
    }

    private static String authenticateUser(UserService userService, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Аутентификация" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        User user = userService.authenticate(username, password);
        if (user != null) {
            return username;
        } else {
            return null;
        }
    }

    private static void mainMenu(Scanner scanner, CarService carService, UserService userService, OrderService orderService, AuditLogService auditLogService, String username) throws SQLException {
        while (true) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            try {
                switch (choice) {
                    case 1:
                        addCar(carService, auditLogService, username, scanner);
                        break;
                    case 2:
                        removeCar(carService, auditLogService, username, scanner);
                        break;
                    case 3:
                        showAvailableCars(carService);
                        break;
                    case 4:
                        updateCar(carService, auditLogService, username, scanner);
                        break;
                    case 5:
                        createOrder(orderService, carService, userService, auditLogService, username, scanner);
                        break;
                    case 6:
                        showAllOrders(orderService);
                        break;
                    case 7:
                        updateOrderStatus(orderService, auditLogService, username, scanner);
                        break;
                    case 8:
                        cancelOrder(orderService, auditLogService, username, scanner);
                        break;
                    case 9:
                        showAllUsers(userService);
                        break;
                    case 10:
                        filterClientsByName(userService, scanner);
                        break;
                    case 11:
                        filterClientsByContactInfo(userService, scanner);
                        break;
                    case 12:
                        sortClientsByName(userService);
                        break;
                    case 13:
                        filterClientsByOrders(userService, scanner);
                        break;
                    case 14:
                        sortClientsByOrders(userService);
                        break;
                    case 15:
                        registerNewUser(userService, scanner);
                        break;
                    case 16:
                        showAuditLogs(auditLogService);
                        break;
                    case 0:
                        System.out.println("Выход из программы.");
                        return;
                    default:
                        System.out.println(ERROR + "Неверный выбор. Попробуйте снова." + RESET);
                }
            } catch (SQLException e) {
                System.out.println(ERROR + "Ошибка базы данных: " + e.getMessage() + RESET);
            }
        }
    }

    private static void showMenu() {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Главное меню" + RESET);
        System.out.println(SEPARATOR);
        System.out.println("1. Добавить автомобиль");
        System.out.println("2. Удалить автомобиль");
        System.out.println("3. Показать доступные автомобили");
        System.out.println("4. Обновить информацию об автомобиле");
        System.out.println("5. Создать заказ");
        System.out.println("6. Показать все заказы");
        System.out.println("7. Обновить статус заказа");
        System.out.println("8. Отменить заказ");
        System.out.println("9. Показать всех пользователей");
        System.out.println("10. Фильтрация клиентов по имени");
        System.out.println("11. Фильтрация клиентов по контактной информации");
        System.out.println("12. Сортировка клиентов по имени");
        System.out.println("13. Фильтрация клиентов по количеству заказов");
        System.out.println("14. Сортировка клиентов по количеству заказов");
        System.out.println("15. Регистрация нового пользователя");
        System.out.println("16. Показать журнал аудита");
        System.out.println("0. Выход");
        System.out.println(SEPARATOR);
        System.out.print("Выберите действие: ");
    }

    private static void addCar(CarService carService, AuditLogService auditLogService, String username, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Добавление нового автомобиля" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите марку: ");
        String make = scanner.nextLine();
        System.out.print("Введите модель: ");
        String model = scanner.nextLine();
        System.out.print("Введите год: ");
        int year = scanner.nextInt();
        System.out.print("Введите цену: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Введите состояние: ");
        String condition = scanner.nextLine();
        carService.addCar(make, model, year, price, condition);
        auditLogService.logAction(username, "Добавил автомобиль: " + make + " " + model);
        System.out.println(SUCCESS + "Автомобиль успешно добавлен." + RESET);
    }

    private static void showAvailableCars(CarService carService) {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Список доступных автомобилей" + RESET);
        System.out.println(SEPARATOR);
        List<Car> availableCars = carService.getAllAvailableCars();
        if (availableCars.isEmpty()) {
            System.out.println(ERROR + "Нет доступных автомобилей." + RESET);
        } else {
            availableCars.forEach(System.out::println);
        }
    }

    private static void removeCar(CarService carService, AuditLogService auditLogService, String username, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Удаление автомобиля" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите ID автомобиля для удаления: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        boolean removed = carService.removeCar(id);
        if (removed) {
            auditLogService.logAction(username, "Удалил автомобиль с ID: " + id);
            System.out.println(SUCCESS + "Автомобиль успешно удален." + RESET);
        } else {
            System.out.println(ERROR + "Не удалось удалить автомобиль." + RESET);
        }
    }

    private static void updateCar(CarService carService, AuditLogService auditLogService, String username, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Обновление информации об автомобиле" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите ID автомобиля для обновления: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Car existingCar = carService.getCarById(id);
        System.out.print("Введите новую марку (текущая: " + existingCar.getMake() + "): ");
        String newMake = scanner.nextLine();
        System.out.print("Введите новую модель (текущая: " + existingCar.getModel() + "): ");
        String newModel = scanner.nextLine();
        System.out.print("Введите новый год (текущий: " + existingCar.getYear() + "): ");
        int newYear = scanner.nextInt();
        System.out.print("Введите новую цену (текущая: " + existingCar.getPrice() + "): ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Введите новое состояние (текущее: " + existingCar.getCondition() + "): ");
        String newCondition = scanner.nextLine();
        Car updatedCar = new Car(newMake, newModel, newYear, newPrice, newCondition);
        boolean updated = carService.updateCar(updatedCar);
        if (updated) {
            auditLogService.logAction(username, "Обновил автомобиль с ID: " + id);
            System.out.println(SUCCESS + "Автомобиль успешно обновлен." + RESET);
        } else {
            System.out.println(ERROR + "Не удалось обновить автомобиль." + RESET);
        }
    }

    private static void createOrder(OrderService orderService, CarService carService, UserService userService, AuditLogService auditLogService, String username, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Создание нового заказа" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите ID автомобиля для заказа: ");
        int carId = scanner.nextInt();
        scanner.nextLine();
        Car car = carService.getCarById(carId);
        if (car == null || !car.isAvailable()) {
            System.out.println(ERROR + "Автомобиль не доступен для заказа." + RESET);
            return;
        }
        System.out.print("Введите имя клиента: ");
        String clientUsername = scanner.nextLine();
        Client client = (Client) userService.authenticate(clientUsername, null);
        if (client == null) {
            System.out.println(ERROR + "Клиент не найден." + RESET);
            return;
        }
        Order order = orderService.createOrder(car, client);
        auditLogService.logAction(username, "Создал заказ с ID: " + order.getId() + " для клиента: " + clientUsername);
        System.out.println(SUCCESS + "Заказ успешно создан с ID: " + order.getId() + RESET);
    }

    private static void showAllOrders(OrderService orderService) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Список всех заказов" + RESET);
        System.out.println(SEPARATOR);
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println(ERROR + "Нет заказов." + RESET);
        } else {
            orders.forEach(System.out::println);
        }
    }

    private static void updateOrderStatus(OrderService orderService, AuditLogService auditLogService, String username, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Обновление статуса заказа" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите ID заказа для обновления: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Введите новый статус: ");
        String status = scanner.nextLine();
        boolean updated = orderService.updateOrderStatus(orderId, status);
        if (updated) {
            auditLogService.logAction(username, "Обновил статус заказа с ID: " + orderId + " на: " + status);
            System.out.println(SUCCESS + "Статус заказа успешно обновлен." + RESET);
        } else {
            System.out.println(ERROR + "Не удалось обновить статус заказа." + RESET);
        }
    }

    private static void cancelOrder(OrderService orderService, AuditLogService auditLogService, String username, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Отмена заказа" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите ID заказа для отмены: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();
        boolean cancelled = orderService.cancelOrder(orderId);
        if (cancelled) {
            auditLogService.logAction(username, "Отменил заказ с ID: " + orderId);
            System.out.println(SUCCESS + "Заказ успешно отменен." + RESET);
        } else {
            System.out.println(ERROR + "Не удалось отменить заказ." + RESET);
        }
    }

    private static void showAllUsers(UserService userService) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Список всех пользователей" + RESET);
        System.out.println(SEPARATOR);
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println(ERROR + "Нет пользователей." + RESET);
        } else {
            users.forEach(System.out::println);
        }
    }

    private static void filterClientsByName(UserService userService, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Фильтрация клиентов по имени" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите имя клиента: ");
        String name = scanner.nextLine();
        List<Client> clients = userService.filterClientsByName(name);
        if (clients.isEmpty()) {
            System.out.println(ERROR + "Нет клиентов с таким именем." + RESET);
        } else {
            clients.forEach(System.out::println);
        }
    }

    private static void filterClientsByContactInfo(UserService userService, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Фильтрация клиентов по контактной информации" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите контактную информацию: ");
        String contactInfo = scanner.nextLine();
        List<Client> clients = userService.filterClientsByContactInfo(contactInfo);
        if (clients.isEmpty()) {
            System.out.println(ERROR + "Нет клиентов с такой контактной информацией." + RESET);
        } else {
            clients.forEach(System.out::println);
        }
    }

    private static void sortClientsByName(UserService userService) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Сортировка клиентов по имени" + RESET);
        System.out.println(SEPARATOR);
        List<Client> clients = userService.sortClientsByName();
        clients.forEach(System.out::println);
    }

    private static void filterClientsByOrders(UserService userService, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Фильтрация клиентов по количеству заказов" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите минимальное количество заказов: ");
        int minOrders = scanner.nextInt();
        System.out.print("Введите максимальное количество заказов: ");
        int maxOrders = scanner.nextInt();
        scanner.nextLine();
        List<Client> clients = userService.filterClientsByOrders(minOrders, maxOrders);
        if (clients.isEmpty()) {
            System.out.println(ERROR + "Нет клиентов с таким количеством заказов." + RESET);
        } else {
            clients.forEach(System.out::println);
        }
    }

    private static void sortClientsByOrders(UserService userService) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Сортировка клиентов по количеству заказов" + RESET);
        System.out.println(SEPARATOR);
        List<Client> clients = userService.sortClientsByOrders();
        clients.forEach(System.out::println);
    }

    private static void registerNewUser(UserService userService, Scanner scanner) throws SQLException {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Регистрация нового пользователя" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        System.out.print("Введите тип пользователя (Admin/Manager/Client): ");
        String userType = scanner.nextLine();

        User user;
        switch (userType.trim().toLowerCase()) {
            case "client":
                System.out.print("Введите контактную информацию клиента: ");
                String contactInfo = scanner.nextLine();
                user = new Client(username, password, contactInfo);
                break;
            case "admin":
                user = new Admin(username, password);
                break;
            case "manager":
                user = new Manager(username, password);
                break;
            default:
                System.out.println("Неверный тип пользователя. Используйте Admin, Manager или Client.");
                return;
        }
        userService.registerUser(user);
        System.out.println(SUCCESS + "Пользователь успешно зарегистрирован." + RESET);
    }

    private static void showAuditLogs(AuditLogService auditLogController) {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Журнал аудита" + RESET);
        System.out.println(SEPARATOR);
        List<AuditLog> logs = auditLogController.getAllLogs();
        if (logs.isEmpty()) {
            System.out.println(ERROR + "Нет записей в журнале аудита." + RESET);
        } else {
            logs.forEach(System.out::println);
        }
    }
}