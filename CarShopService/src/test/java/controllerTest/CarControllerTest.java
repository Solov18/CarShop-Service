//package controllerTest;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.example.controller.CarController;
//import org.example.dto.CarDTO;
//import org.example.service.CarService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import java.io.BufferedReader;
//import java.io.PrintWriter;
//import java.io.StringReader;
//import java.util.Collections;
//import java.util.List;
//
//
//public class CarControllerTest {
//
//    @Mock
//    private CarService carService;
//
//    @InjectMocks
//    private CarController carController;
//
//    private HttpServletRequest request;
//    private HttpServletResponse response;
//    private PrintWriter writer;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        MockitoAnnotations.openMocks(this);
//        request = mock(HttpServletRequest.class);
//        response = mock(HttpServletResponse.class);
//        writer = mock(PrintWriter.class);
//        when(response.getWriter()).thenReturn(writer);
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    public void testDoGetCarById() throws Exception {
//
//        CarDTO carDTO = new CarDTO();
//        when(request.getParameter("id")).thenReturn("1");
//        when(carService.getCarById(1)).thenReturn(carDTO);
//
//
//        carController.doGet(request, response);
//
//
//        verify(response).setContentType("application/json");
//        verify(carService).getCarById(1);
//    }
//
//    @Test
//    public void testDoGetCarByIdNotFound() throws Exception {
//
//        when(request.getParameter("id")).thenReturn("1");
//        when(carService.getCarById(1)).thenThrow(new RuntimeException("Car not found"));
//
//
//        carController.doGet(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Car not found");
//    }
//
//    @Test
//    public void testDoGetSearchCars() throws Exception {
//        List<CarDTO> carsDTO = Collections.singletonList(new CarDTO());
//        when(request.getParameter("make")).thenReturn("Toyota");
//        when(request.getParameter("model")).thenReturn("Corolla");
//        when(carService.searchCars("Toyota", "Corolla", null, null, null, null)).thenReturn(carsDTO);
//
//        carController.doGet(request, response);
//
//        verify(response).setContentType("application/json");
//        verify(objectMapper).writeValue(writer, carsDTO);
//    }
//
//
//
//    @Test
//    public void testDoGetAllAvailableCars() throws Exception {
//        List<CarDTO> carsDTO = Collections.singletonList(new CarDTO());
//        when(request.getParameter("make")).thenReturn(null);
//        when(request.getParameter("model")).thenReturn(null);
//        when(carService.getAllAvailableCars()).thenReturn(carsDTO);
//
//        carController.doGet(request, response);
//        verify(response).setContentType("application/json");
//        verify(objectMapper).writeValue(writer, carsDTO);
//    }
//
//
//
//    @Test
//    public void testDoPost() throws Exception {
//
//        CarDTO carDTO = new CarDTO();
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"make\":\"Toyota\",\"model\":\"Corolla\"}")));
//
//
//        carController.doPost(request, response);
//
//
//        verify(carService).addCar(any(CarDTO.class));
//        verify(response).setStatus(HttpServletResponse.SC_CREATED);
//    }
//
//    @Test
//    public void testDoPutSuccess() throws Exception {
//
//        CarDTO carDTO = new CarDTO();
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1,\"make\":\"Toyota\",\"model\":\"Camry\"}")));
//        when(carService.updateCar(any(CarDTO.class))).thenReturn(true);
//
//        carController.doPut(request, response);
//
//
//        verify(response).setStatus(HttpServletResponse.SC_OK);
//    }
//
//    @Test
//    public void testDoPutNotFound() throws Exception {
//
//        CarDTO carDTO = new CarDTO();
//        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("{\"id\":1,\"make\":\"Toyota\",\"model\":\"Camry\"}")));
//        when(carService.updateCar(any(CarDTO.class))).thenReturn(false);
//
//
//        carController.doPut(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Автомобиль не найден для обновления");
//    }
//
//    @Test
//    public void testDoDeleteSuccess() throws Exception {
//
//        when(request.getParameter("id")).thenReturn("1");
//        when(carService.removeCar(anyInt())).thenReturn(true);
//
//
//        carController.doDelete(request, response);
//
//
//        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
//    }
//
//    @Test
//    public void testDoDeleteNotFound() throws Exception {
//
//        when(request.getParameter("id")).thenReturn("1");
//        when(carService.removeCar(anyInt())).thenReturn(false);
//
//
//        carController.doDelete(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Автомобиль не найден для удаления");
//    }
//
//    @Test
//    public void testDoDeleteBadRequest() throws Exception {
//
//        when(request.getParameter("id")).thenReturn(null);
//
//
//        carController.doDelete(request, response);
//
//
//        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "ID автомобиля не указан");
//    }
//}