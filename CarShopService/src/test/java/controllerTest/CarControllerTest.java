package controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.CarController;
import org.example.dto.CarDTO;
import org.example.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.Collections;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тестовый класс для проверки функциональности {@link CarController}.
 * Использует {@link MockMvc} для имитации HTTP-запросов к контроллеру.
 * Мокирует взаимодействие с сервисным слоем через {@link CarService}.
 */
public class CarControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CarService carService;

    @InjectMocks
    private CarController carController;

    private ObjectMapper objectMapper;

    /**
     * Метод, выполняемый перед запуском каждого теста.
     * Инициализирует моки и настраивает {@link MockMvc} для работы с контроллером.
     */
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    /**
     * Тестирует получение списка всех автомобилей.
     * Проверяет, что метод возвращает корректный список автомобилей с правильным HTTP-статусом.
     *
     * @throws Exception если возникает ошибка при выполнении запроса
     */
    @Test
    public void testGetAllCars() throws Exception {
        CarDTO car1 = new CarDTO(1, "Toyota", "Camry", 2020, 20000, "New", true);
        CarDTO car2 = new CarDTO(2, "Honda", "Accord", 2021, 25000, "New",true);

        when(carService.getAllAvailableCars()).thenReturn(Arrays.asList(car1, car2));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Toyota")))
                .andExpect(content().string(containsString("Honda")));

        verify(carService, times(1)).getAllAvailableCars();
    }

    /**
     * Тестирует получение автомобиля по его идентификатору (ID).
     * Проверяет, что возвращаемый автомобиль соответствует ожидаемому, и проверяет правильность HTTP-статуса.
     *
     * @throws Exception если возникает ошибка при выполнении запроса
     */
    @Test
    public void testGetCarById() throws Exception {
        CarDTO car = new CarDTO(1, "Toyota", "Camry", 2020, 20000, "New",true);

        when(carService.getCarById(1)).thenReturn(car);

        mockMvc.perform(get("/api/cars?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Toyota")));

        verify(carService, times(1)).getCarById(1);
    }

    /**
     * Тестирует добавление нового автомобиля.
     * Проверяет успешное создание автомобиля с корректным статусом и вызов метода сервиса.
     *
     * @throws Exception если возникает ошибка при выполнении запроса
     */
    @Test
    public void testAddCar() throws Exception {
        CarDTO car = new CarDTO(null, "Toyota", "Camry", 2020, 20000, "New",true);

        when(carService.addCar(any(CarDTO.class))).thenReturn(car);

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isCreated());

        verify(carService, times(1)).addCar(any(CarDTO.class));
    }

    /**
     * Тестирует обновление данных об автомобиле.
     * Проверяет, что обновление выполнено успешно, и возвращается корректное сообщение и статус.
     *
     * @throws Exception если возникает ошибка при выполнении запроса
     */
    @Test
    public void testUpdateCar() throws Exception {
        CarDTO car = new CarDTO(1, "Toyota", "Camry", 2020, 20000, "New",true);

        when(carService.updateCar(any(CarDTO.class))).thenReturn(true);

        mockMvc.perform(put("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk())
                .andExpect(content().string("Автомобиль успешно обновлён"));

        verify(carService, times(1)).updateCar(any(CarDTO.class));
    }

    /**
     * Тестирует удаление автомобиля по идентификатору (ID).
     * Проверяет, что автомобиль успешно удален и возвращается корректный HTTP-статус.
     *
     * @throws Exception если возникает ошибка при выполнении запроса
     */
    @Test
    public void testDeleteCar() throws Exception {
        when(carService.removeCar(1)).thenReturn(true);

        mockMvc.perform(delete("/api/cars?id=1"))
                .andExpect(status().isNoContent());

        verify(carService, times(1)).removeCar(1);
    }

    /**
     * Тестирует получение автомобилей по диапазону цен.
     * Проверяет, что автомобили, попадающие в заданный диапазон, возвращаются корректно.
     *
     * @throws Exception если возникает ошибка при выполнении запроса
     */
    @Test
    public void testGetCarsByPriceRange() throws Exception {
        CarDTO car = new CarDTO(1, "Toyota", "Camry", 2020, 20000, "New",true);

        when(carService.getCarsByPriceRange(10000, 30000)).thenReturn(Collections.singletonList(car));

        mockMvc.perform(get("/api/cars/price-range")
                        .param("minPrice", "10000")
                        .param("maxPrice", "30000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Toyota")));

        verify(carService, times(1)).getCarsByPriceRange(10000, 30000);
    }
}
