package org.example;
import org.example.controller.CarController;
import org.example.controller.OrderController;
import org.example.controller.UserController;
import org.example.logi.AuditLog;
import org.example.logi.AuditLogController;
import org.example.model.*;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class CarShopApp {
    private static final String LINE_SEPARATOR = "ஜ___________________ஜ۩۞۩ஜ____________________ஜ";
    private static final String SEPARATOR = "ஜ_________________________________________________ஜ";
    private static final String HIGHLIGHT = "\u001B[33m";
    private static final String ERROR = "\u001B[31m";
    private static final String SUCCESS = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        CarController carController = new CarController();
        UserController userController = new UserController();
        OrderController orderController = new OrderController(userController);
        AuditLogController auditLogController = new AuditLogController();

        // Добавление пользователей для тестирования
        userController.registerUser(new Admin("admin", "123"));
        userController.registerUser(new Manager("manager", "123"));
        userController.registerUser(new Client("client", "123", "client@carshop.com"));
        userController.registerUser(new Client("client1", "123", "client1@carshop.com"));
        userController.registerUser(new Client("client2", "123", "client2@carshop.com"));

        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        User loggedInUser = null;

        while (loggedInUser == null) {
            System.out.println(LINE_SEPARATOR);
            System.out.println(HIGHLIGHT + "          Аутентификация пользователя" + RESET);
            System.out.println(SEPARATOR);
            System.out.print("Введите имя пользователя: ");
            String username = scanner.nextLine();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();
            loggedInUser = userController.authenticate(username, password);

            if (loggedInUser == null) {
                System.out.println(ERROR + "Ошибка аутентификации. Пользователь не найден." + RESET);
                System.out.print("Хотите зарегистрироваться? (да/нет): ");
                String response = scanner.nextLine();

                if ("да".equalsIgnoreCase(response)) {
                    registerNewUser(userController, auditLogController, scanner, null);
                    System.out.println(HIGHLIGHT + "Попробуйте войти снова." + RESET);
                } else {
                    System.out.println("Завершение работы.");
                    scanner.close();
                    return;
                }
            } else {
                auditLogController.logAction(loggedInUser.getUsername(), "вход в систему");
            }
        }

        while (running) {
            System.out.println(LINE_SEPARATOR);
            System.out.println(HIGHLIGHT + "Меню пользователя: " + loggedInUser.getUsername() + RESET);
            System.out.println(SEPARATOR);
            System.out.println("1. Добавить автомобиль");
            System.out.println("2. Показать все доступные автомобили");
            System.out.println("3. Удалить автомобиль");
            System.out.println("4. Обновить автомобиль");
            System.out.println("5. Сохранить автомобили в файл");
            System.out.println("6. Загрузить автомобили из файла");
            System.out.println("7. Создать заказ");
            System.out.println("8. Показать все заказы");
            System.out.println("9. Обновить статус заказа");
            System.out.println("10. Отменить заказ");
            System.out.println("11. Показать всех пользователей");
            System.out.println("12. Показать журнал действий");
            System.out.println("13. Зарегистрировать нового пользователя");
            System.out.println("14. Фильтр автомобилей по цене");
            System.out.println("15. Фильтр автомобилей по году выпуска");
            System.out.println("16. Поиск заказов по году и месяцу");
            System.out.println("17. Поиск заказов по клиенту");
            System.out.println("18. Поиск заказов по статусу");
            System.out.println("19. Поиск заказов по автомобилю");
            System.out.println("20. Фильтрация клиентов по имени");
            System.out.println("21. Фильтрация клиентов по контактной информации");
            System.out.println("22. Фильтрация клиентов по количеству заказов");
            System.out.println("23. Сортировка клиентов по имени");
            System.out.println("24. Сортировка клиентов по количеству заказов");
            System.out.println("0. Выход");
            System.out.print("Введите ваш выбор: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Manager")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
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
                    carController.addCar(make, model, year, price, condition);
                    auditLogController.logAction(loggedInUser.getUsername(), "добавлен автомобиль: " + make + " " + model);
                    System.out.println(SUCCESS + "Автомобиль успешно добавлен." + RESET);
                    break;
                case 2:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Список доступных автомобилей" + RESET);
                    System.out.println(SEPARATOR);
                    List<Car> availableCars = carController.getAllAvailableCars();
                    if (availableCars.isEmpty()) {
                        System.out.println(ERROR + "Нет доступных автомобилей." + RESET);
                    } else {
                        for (Car car : availableCars) {
                            System.out.println(car);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "просмотрены доступные автомобили");
                    break;
                case 3:
                    if (!loggedInUser.hasRole("Admin")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Удаление автомобиля" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите индекс автомобиля для удаления: ");
                    int removeIndex = scanner.nextInt();
                    scanner.nextLine();
                    if (carController.removeCar(removeIndex)) {
                        System.out.println(SUCCESS + "Автомобиль успешно удален." + RESET);
                    } else {
                        System.out.println(ERROR + "Автомобиль не найден." + RESET);
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "удален автомобиль с индексом: " + removeIndex);
                    break;
                case 4:
                    if (!loggedInUser.hasRole("Admin")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Обновление автомобиля" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите индекс автомобиля для обновления: ");
                    int updateIndex = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Введите новую марку: ");
                    String newMake = scanner.nextLine();
                    System.out.print("Введите новую модель: ");
                    String newModel = scanner.nextLine();
                    System.out.print("Введите новый год: ");
                    int newYear = scanner.nextInt();
                    System.out.print("Введите новую цену: ");
                    double newPrice = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Введите новое состояние: ");
                    String newCondition = scanner.nextLine();
                    if (carController.updateCar(updateIndex, newMake, newModel, newYear, newPrice, newCondition)) {
                        System.out.println(SUCCESS + "Информация об автомобиле успешно обновлена." + RESET);
                    } else {
                        System.out.println(ERROR + "Автомобиль не найден." + RESET);
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "обновлен автомобиль с индексом: " + updateIndex);
                    break;
                case 5:
                    if (!loggedInUser.hasRole("Admin")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Сохранение автомобилей в файл" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите имя файла для сохранения: ");
                    String saveFilename = scanner.nextLine();
                    try {
                        carController.saveCarsToFile(saveFilename);
                        System.out.println(SUCCESS + "Автомобили успешно сохранены." + RESET);
                    } catch (IOException e) {
                        System.out.println(ERROR + "Ошибка при сохранении автомобилей в файл: " + e.getMessage() + RESET);
                    }
                    break;
                case 6:
                    if (!loggedInUser.hasRole("Admin")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Загрузка автомобилей из файла " + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите имя файла для загрузки: ");
                    String loadFilename = scanner.nextLine();
                    try {
                        carController.loadCarsFromFile(loadFilename);
                        System.out.println(SUCCESS + "Автомобили успешно загружены." + RESET);
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println(ERROR + "Ошибка при загрузке автомобилей из файла: " + e.getMessage() + RESET);
                    }
                    break;
                case 7:
                    if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Client")) {
                        System.out.println(SEPARATOR);
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Создание заказа" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите имя пользователя клиента: ");
                    String clientUsername = scanner.nextLine();
                    Client client = (Client) userController.getAllUsers().stream()
                            .filter(user -> user.getUsername().equals(clientUsername) && user instanceof Client)
                            .findFirst()
                            .orElse(null);
                    if (client == null) {
                        System.out.println(ERROR + "Клиент не найден." + RESET);
                        break;
                    }
                    System.out.print("Введите индекс автомобиля для заказа: ");
                    int carIndex = scanner.nextInt();
                    scanner.nextLine();
                    Car car = carController.getCarById(carIndex);
                    if (car == null || !car.isAvailable()) {
                        System.out.println(ERROR + "Автомобиль недоступен." + RESET);
                        break;
                    }
                    orderController.createOrder(car, client);
                    auditLogController.logAction(loggedInUser.getUsername(), "создан заказ на автомобиль с индексом: " + carIndex);
                    System.out.println(SUCCESS + "Заказ успешно создан." + RESET);
                    break;

                case 8:
                    if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Manager")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Показать все заказы" + RESET);
                    System.out.println(SEPARATOR);
                    List<Order> orders = orderController.getAllOrders();
                    if (orders.isEmpty()) {
                        System.out.println(ERROR + "Нет заказов." + RESET);
                    } else {
                        for (Order order : orders) {
                            System.out.println(order);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "просмотрены все заказы");
                    break;
                case 9:
                    if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Manager")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Обновление статуса заказа" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите ID заказа для обновления статуса: ");
                    int orderId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Введите новый статус: ");
                    String newStatus = scanner.nextLine();
                    if (orderController.updateOrderStatus(orderId, newStatus)) {
                        System.out.println(SUCCESS + "Статус заказа успешно обновлен." + RESET);
                    } else {
                        System.out.println(ERROR + "Заказ не найден." + RESET);
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "обновлен статус заказа с ID: " + orderId);
                    break;
                case 10:
                    if (!loggedInUser.hasRole("Admin") && !loggedInUser.hasRole("Manager")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Отмена заказа" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите ID заказа для отмены: ");
                    int cancelOrderId = scanner.nextInt();
                    scanner.nextLine();
                    if (orderController.cancelOrder(cancelOrderId)) {
                        System.out.println(SUCCESS + "Заказ успешно отменен." + RESET);
                    } else {
                        System.out.println(ERROR + "Заказ не найден." + RESET);
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "отменен заказ с ID: " + cancelOrderId);
                    break;
                case 11:
                    if (!loggedInUser.hasRole("Admin")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Список всех пользователей" + RESET);
                    System.out.println(SEPARATOR);
                    List<User> users = userController.getAllUsers();
                    if (users.isEmpty()) {
                        System.out.println(ERROR + "Нет зарегистрированных пользователей." + RESET);
                    } else {
                        for (User user : users) {
                            System.out.println(user);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "просмотрены все пользователи");
                    break;
                case 12:
                    if (!loggedInUser.hasRole("Admin")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Журнал действий" + RESET);
                    System.out.println(SEPARATOR);
                    List<AuditLog> logs = auditLogController.getAllLogs();
                    if (logs.isEmpty()) {
                        System.out.println(ERROR + "Журнал действий пуст." + RESET);
                    } else {
                        for (AuditLog log : logs) {
                            System.out.println(log);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "просмотрен журнал действий");
                    break;
                case 13:
                    if (!loggedInUser.hasRole("Admin")) {
                        System.out.println(ERROR + "Недостаточно прав для выполнения этой операции." + RESET);
                        break;
                    }
                    registerNewUser(userController, auditLogController, scanner, loggedInUser);
                    break;
                case 14:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Фильтр автомобилей по цене" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите минимальную цену: ");
                    double minPrice = scanner.nextDouble();
                    System.out.print("Введите максимальную цену: ");
                    double maxPrice = scanner.nextDouble();
                    scanner.nextLine();
                    List<Car> filteredCarsByPrice = carController.getCarsByPriceRange(minPrice, maxPrice);
                    if (filteredCarsByPrice.isEmpty()) {
                        System.out.println(ERROR + "Нет автомобилей в заданном ценовом диапазоне." + RESET);
                    } else {
                        for (Car cars : filteredCarsByPrice) {
                            System.out.println(cars);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнен фильтр автомобилей по цене");
                    break;
                case 15:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Фильтр автомобилей по году выпуска" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите минимальный год выпуска: ");
                    int minYear = scanner.nextInt();
                    System.out.print("Введите максимальный год выпуска: ");
                    int maxYear = scanner.nextInt();
                    scanner.nextLine();
                    List<Car> filteredCarsByYear = carController.getCarsByYearRange(minYear, maxYear);
                    if (filteredCarsByYear.isEmpty()) {
                        System.out.println(ERROR + "Нет автомобилей в заданном диапазоне годов выпуска." + RESET);
                    } else {
                        for (Car cars : filteredCarsByYear) {
                            System.out.println(cars);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнен фильтр автомобилей по году выпуска");
                    break;
                case 16:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Поиск заказов по году и месяцу" + RESET);
                    System.out.println(SEPARATOR);

                    LocalDate startDate = null;
                    LocalDate endDate = null;

                    while (startDate == null) {
                        System.out.print("Введите начальный год и месяц (гггг-мм): ");
                        try {
                            String input = scanner.nextLine();
                            startDate = LocalDate.parse(input + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        } catch (DateTimeException e) {
                            System.out.println(ERROR + "Неверный формат даты. Пожалуйста, используйте формат гггг-мм." + RESET);
                        }
                    }

                    while (endDate == null) {
                        System.out.print("Введите конечный год и месяц (гггг-мм): ");
                        try {
                            String input = scanner.nextLine();
                            endDate = LocalDate.parse(input + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        } catch (DateTimeException e) {
                            System.out.println(ERROR + "Неверный формат даты. Пожалуйста, используйте формат гггг-мм." + RESET);
                        }
                    }

                    if (startDate.isAfter(endDate)) {
                        System.out.println(ERROR + "Начальная дата не может быть позже конечной даты." + RESET);
                    } else {
                        LocalDate endOfMonthDate = endDate.withDayOfMonth(endDate.lengthOfMonth());
                        List<Order> ordersByDate = orderController.getOrdersByDateRange(startDate.atStartOfDay(), endOfMonthDate.atTime(23, 59, 59));
                        if (ordersByDate.isEmpty()) {
                            System.out.println(ERROR + "Нет заказов в заданном диапазоне дат." + RESET);
                        } else {
                            for (Order order : ordersByDate) {
                                System.out.println(order);
                            }
                        }
                        auditLogController.logAction(loggedInUser.getUsername(), "выполнен поиск заказов по году и месяцу");
                    }
                    break;
                case 17:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Поиск заказов по клиенту" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите имя пользователя клиента: ");
                    String searchClientUsername = scanner.nextLine();
                    List<Order> ordersByClient = orderController.getOrdersByClient(searchClientUsername);
                    if (ordersByClient.isEmpty()) {
                        System.out.println(ERROR + "Нет заказов для указанного клиента." + RESET);
                    } else {
                        for (Order order : ordersByClient) {
                            System.out.println(order);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнен поиск заказов по клиенту");
                    break;
                case 18:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Поиск заказов по статусу" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите статус заказа: ");
                    String searchStatus = scanner.nextLine();
                    List<Order> ordersByStatus = orderController.getOrdersByStatus(searchStatus);
                    if (ordersByStatus.isEmpty()) {
                        System.out.println(ERROR + "Нет заказов с указанным статусом." + RESET);
                    } else {
                        for (Order order : ordersByStatus) {
                            System.out.println(order);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнен поиск заказов по статусу");
                    break;
                case 19:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Поиск заказов по автомобилю" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите ID автомобиля: ");
                    int searchCarId = scanner.nextInt();
                    scanner.nextLine();
                    List<Order> ordersByCar = orderController.getOrdersByCar(searchCarId);
                    if (ordersByCar.isEmpty()) {
                        System.out.println(ERROR + "Нет заказов для указанного автомобиля." + RESET);
                    } else {
                        for (Order order : ordersByCar) {
                            System.out.println(order);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнен поиск заказов по автомобилю");
                    break;
                case 20:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Фильтрация клиентов по имени" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите имя для фильтрации: ");
                    String filterName = scanner.nextLine();
                    List<Client> filteredClientsByName = userController.filterClientsByName(filterName);
                    if (filteredClientsByName.isEmpty()) {
                        System.out.println(ERROR + "Нет клиентов с указанным именем." + RESET);
                    } else {
                        for (Client clientByName : filteredClientsByName) {
                            System.out.println(clientByName);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнена фильтрация клиентов по имени");
                    break;

                case 21:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Фильтрация клиентов по контактной информации" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите контактную информацию для фильтрации: ");
                    String filterContactInfo = scanner.nextLine();
                    List<Client> filteredClientsByContact = userController.filterClientsByContactInfo(filterContactInfo);
                    if (filteredClientsByContact.isEmpty()) {
                        System.out.println(ERROR + "Нет клиентов с указанной контактной информацией." + RESET);
                    } else {
                        for (Client clientByContact : filteredClientsByContact) {
                            System.out.println(clientByContact);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнена фильтрация клиентов по контактной информации");
                    break;

                case 22:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Фильтрация клиентов по количеству заказов" + RESET);
                    System.out.println(SEPARATOR);
                    System.out.print("Введите минимальное количество заказов: ");
                    int minOrders = scanner.nextInt();
                    System.out.print("Введите максимальное количество заказов: ");
                    int maxOrders = scanner.nextInt();
                    scanner.nextLine();
                    List<Client> filteredClientsByOrders = userController.filterClientsByOrders(minOrders, maxOrders);
                    if (filteredClientsByOrders.isEmpty()) {
                        System.out.println(ERROR + "Нет клиентов в указанном диапазоне количества заказов." + RESET);
                    } else {
                        for (Client clientByOrders : filteredClientsByOrders) {
                            System.out.println(clientByOrders);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнена фильтрация клиентов по количеству заказов");
                    break;

                case 23:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Сортировка клиентов по имени" + RESET);
                    System.out.println(SEPARATOR);
                    List<Client> sortedClientsByName = userController.sortClientsByName();
                    if (sortedClientsByName.isEmpty()) {
                        System.out.println(ERROR + "Нет клиентов для сортировки." + RESET);
                    } else {
                        for (Client clientByName : sortedClientsByName) {
                            System.out.println(clientByName);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнена сортировка клиентов по имени");
                    break;

                case 24:
                    System.out.println(LINE_SEPARATOR);
                    System.out.println(HIGHLIGHT + "Сортировка клиентов по количеству заказов" + RESET);
                    System.out.println(SEPARATOR);
                    List<Client> sortedClientsByOrders = userController.sortClientsByOrders();
                    if (sortedClientsByOrders.isEmpty()) {
                        System.out.println(ERROR + "Нет клиентов для сортировки." + RESET);
                    } else {
                        for (Client clientByOrders : sortedClientsByOrders) {
                            System.out.println(clientByOrders);
                        }
                    }
                    auditLogController.logAction(loggedInUser.getUsername(), "выполнена сортировка клиентов по количеству заказов");
                    break;
                case 0:
                    running = false;
                    auditLogController.logAction(loggedInUser.getUsername(), "выход из системы");
                    System.out.println(SUCCESS + "Выход из системы. До свидания!" + RESET);
                    break;
                default:
                    System.out.println(ERROR + "Недопустимый выбор. Попробуйте еще раз." + RESET);
                    break;
            }
        }

        scanner.close();
    }

    public static void registerNewUser(UserController userController, AuditLogController auditLogController, Scanner scanner, User loggedInUser) {
        System.out.println(LINE_SEPARATOR);
        System.out.println(HIGHLIGHT + "Регистрация нового пользователя" + RESET);
        System.out.println(SEPARATOR);
        System.out.print("Введите тип пользователя (Admin, Manager, Client): ");
        String userType = scanner.nextLine();
        System.out.print("Введите имя пользователя: ");
        String newUserUsername = scanner.nextLine();
        System.out.print("Введите пароль пользователя: ");
        String newUserPassword = scanner.nextLine();

        User newUser;
        switch (userType.trim()) {
            case "Admin":
                newUser = new Admin(newUserUsername, newUserPassword);
                break;
            case "Manager":
                newUser = new Manager(newUserUsername, newUserPassword);
                break;
            case "Client":
                System.out.print("Введите контактную информацию клиента: ");
                String contactInfo = scanner.nextLine();

                newUser = new Client(newUserUsername, newUserPassword, contactInfo);
                break;
            default:
                System.out.println(ERROR + "Некорректный тип пользователя." + RESET);
                return;
        }

        if (userController.userExists(newUserUsername)) {
            System.out.println(ERROR + "Пользователь с таким именем уже существует." + RESET);
            return;
        }

        userController.registerUser(newUser);
        if (loggedInUser != null) {
            auditLogController.logAction(loggedInUser.getUsername(), "зарегистрирован новый пользователь: " + newUserUsername);
        }
        System.out.println(SUCCESS + "Новый пользователь успешно зарегистрирован." + RESET);
    }
}