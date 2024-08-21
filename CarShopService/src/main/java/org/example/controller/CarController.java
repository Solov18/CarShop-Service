package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.config.DatabaseConnectionManager;
import org.example.dto.CarDTO;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * Сервлет для управления автомобильными данными через REST API.
 * Обрабатывает запросы для получения, добавления, обновления и удаления автомобилей.
 */
/**
 * Класс CarController является сервлетом, который обрабатывает HTTP-запросы, связанные с автомобилями.
 * Он предоставляет действия для получения всех доступных автомобилей, поиска по параметрам, добавления, обновления и удаления автомобилей.
 */
@WebServlet("/api/cars")
public class CarController extends HttpServlet {
    private CarService carService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Инициализация сервлета и зависимостей.
     * Вызывается при запуске сервлета.
     *
     * @throws ServletException если произошла ошибка во время инициализации.
     */
    @Override
    public void init() throws ServletException {
        super.init();

        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager();
        CarRepository carRepository = new CarRepository(dbConnectionManager);
        this.carService = new CarService(carRepository);
    }

    /**
     * Обрабатывает GET-запросы для получения информации об автомобилях.
     * Может возвращать автомобиль по ID, список всех доступных автомобилей или выполнять поиск по параметрам.
     *
     * @param req  объект запроса.
     * @param resp объект ответа.
     * @throws ServletException если произошла ошибка в процессе обработки.
     * @throws IOException      если произошла ошибка при работе с I/O.
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");

        // Используем Objects.isNull для улучшенной читабельности
        if (Objects.nonNull(idParam)) {

            int id = Integer.parseInt(idParam);
            try {
                CarDTO carDTO = carService.getCarById(id);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), carDTO);
            } catch (RuntimeException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            }
        } else if (Objects.nonNull(req.getParameter("make")) || Objects.nonNull(req.getParameter("model")) ||
                Objects.nonNull(req.getParameter("year")) || Objects.nonNull(req.getParameter("minPrice")) ||
                Objects.nonNull(req.getParameter("maxPrice")) || Objects.nonNull(req.getParameter("condition"))) {

            String make = req.getParameter("make");
            String model = req.getParameter("model");
            Integer year = getIntParameter(req, "year");
            Double minPrice = getDoubleParameter(req, "minPrice");
            Double maxPrice = getDoubleParameter(req, "maxPrice");
            String condition = req.getParameter("condition");

            List<CarDTO> carsDTO = carService.searchCars(make, model, year, minPrice, maxPrice, condition);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), carsDTO);
        } else {

            List<CarDTO> carsDTO = carService.getAllAvailableCars();
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getWriter(), carsDTO);
        }
    }

    /**
     * Обрабатывает POST-запросы для добавления нового автомобиля.
     *
     * @param req  объект запроса.
     * @param resp объект ответа.
     * @throws ServletException если произошла ошибка в процессе обработки.
     * @throws IOException      если произошла ошибка при работе с I/O.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CarDTO carDTO = objectMapper.readValue(req.getReader(), CarDTO.class);
        carService.addCar(carDTO);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    /**
     * Обрабатывает PUT-запросы для обновления данных об автомобиле.
     *
     * @param req  объект запроса.
     * @param resp объект ответа.
     * @throws ServletException если произошла ошибка в процессе обработки.
     * @throws IOException      если произошла ошибка при работе с I/O.
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CarDTO carDTO = objectMapper.readValue(req.getReader(), CarDTO.class);
        boolean updated = carService.updateCar(carDTO);
        if (updated) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Автомобиль не найден для обновления");
        }
    }

    /**
     * Обрабатывает DELETE-запросы для удаления автомобиля по его ID.
     *
     * @param req  объект запроса.
     * @param resp объект ответа.
     * @throws ServletException если произошла ошибка в процессе обработки.
     * @throws IOException      если произошла ошибка при работе с I/O.
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (Objects.nonNull(idParam)) {
            int id = Integer.parseInt(idParam);
            boolean removed = carService.removeCar(id);
            if (removed) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Автомобиль не найден для удаления");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID автомобиля не указан");
        }
    }

    /**
     * Получает целочисленный параметр из запроса.
     *
     * @param req       объект запроса.
     * @param paramName имя параметра.
     * @return значение параметра или null, если параметр отсутствует.
     */
    public Integer getIntParameter(HttpServletRequest req, String paramName) {
        String param = req.getParameter(paramName);
        return (Objects.nonNull(param) && !param.isEmpty()) ? Integer.parseInt(param) : null;
    }

    /**
     * Получает числовой параметр с плавающей точкой из запроса.
     *
     * @param req       объект запроса.
     * @param paramName имя параметра.
     * @return значение параметра или null, если параметр отсутствует.
     */
    public Double getDoubleParameter(HttpServletRequest req, String paramName) {
        String param = req.getParameter(paramName);
        return (Objects.nonNull(param) && !param.isEmpty()) ? Double.parseDouble(param) : null;
    }
}