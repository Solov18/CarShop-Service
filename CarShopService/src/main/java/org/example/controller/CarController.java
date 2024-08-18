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



@WebServlet("/api/cars")
public class CarController extends HttpServlet {
    private CarService carService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();

        DatabaseConnectionManager dbConnectionManager = new DatabaseConnectionManager();
        CarRepository carRepository = new CarRepository(dbConnectionManager);
        this.carService = new CarService(carRepository);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {

            int id = Integer.parseInt(idParam);
            try {
                CarDTO carDTO = carService.getCarById(id);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getWriter(), carDTO);
            } catch (RuntimeException e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            }
        } else if (req.getParameter("make") != null || req.getParameter("model") != null || req.getParameter("year") != null ||
                req.getParameter("minPrice") != null || req.getParameter("maxPrice") != null || req.getParameter("condition") != null) {

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

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CarDTO carDTO = objectMapper.readValue(req.getReader(), CarDTO.class);
        carService.addCar(carDTO);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

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

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
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

    public Integer getIntParameter(HttpServletRequest req, String paramName) {
        String param = req.getParameter(paramName);
        return (param != null && !param.isEmpty()) ? Integer.parseInt(param) : null;
    }

    public Double getDoubleParameter(HttpServletRequest req, String paramName) {
        String param = req.getParameter(paramName);
        return (param != null && !param.isEmpty()) ? Double.parseDouble(param) : null;
    }
}