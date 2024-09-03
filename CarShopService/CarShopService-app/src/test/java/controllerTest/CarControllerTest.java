package controllerTest;

import org.example.controller.CarController;
import org.example.dto.CarDTO;
import org.example.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
public class CarControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @InjectMocks
    private CarController carController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(carController).build();
    }

    @Test
    public void testGetCars() throws Exception {
        CarDTO car1 = new CarDTO(1, "Toyota", "Camry", 2020, 25000.0, "New",true);
        CarDTO car2 = new CarDTO(2, "Honda", "Accord", 2019, 22000.0, "New",true);

        given(carService.getAllAvailableCars()).willReturn(Arrays.asList(car1, car2));

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(car1.getId()))
                .andExpect(jsonPath("$[0].make").value(car1.getMake()))
                .andExpect(jsonPath("$[1].id").value(car2.getId()))
                .andExpect(jsonPath("$[1].make").value(car2.getMake()));
    }

    @Test
    public void testGetCarById() throws Exception {
        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2020, 25000.0, "New",true);

        given(carService.getCarById(anyInt())).willReturn(carDTO);

        mockMvc.perform(get("/api/cars")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carDTO.getId()))
                .andExpect(jsonPath("$.make").value(carDTO.getMake()));
    }

    @Test
    public void testAddCar() throws Exception {
        CarDTO carDTO = new CarDTO(1, "Toyota", "Camry", 2020, 25000.0, "New",true);
        given(carService.addCar(any(CarDTO.class))).willReturn(carDTO);

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"make\":\"Toyota\",\"model\":\"Camry\",\"year\":2020,\"price\":25000.0,\"condition\":\"New\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(carDTO.getId()))
                .andExpect(jsonPath("$.make").value(carDTO.getMake()));
    }

    @Test
    public void testUpdateCar() throws Exception {
        given(carService.updateCar(any(CarDTO.class))).willReturn(true);

        mockMvc.perform(put("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"make\":\"Toyota\",\"model\":\"Camry\",\"year\":2020,\"price\":25000.0,\"condition\":\"New\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Автомобиль успешно обновлён"));
    }

    @Test
    public void testDeleteCar() throws Exception {
        given(carService.removeCar(anyInt())).willReturn(true);

        mockMvc.perform(delete("/api/cars")
                        .param("id", "1"))
                .andExpect(status().isNoContent());
    }
}