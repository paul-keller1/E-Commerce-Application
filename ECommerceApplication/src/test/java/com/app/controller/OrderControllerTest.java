package com.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.app.dto.OrderDTO;
import com.app.dto.OrderResponse;
import com.app.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderDTO = new OrderDTO();
        orderDTO.setOrderId(1L);
        orderDTO.setTotalAmount(100.0);
    }

    @Test
    void orderProducts_ShouldReturnCreatedOrder() {
        when(orderService.placeOrder("email@test.com", 1L, "CARD")).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.orderProducts("email@test.com", 1L, "CARD");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void getAllOrders_ShouldReturnResponse() {
        OrderResponse responseDto = new OrderResponse(List.of(orderDTO), 0, 5, 1L, 1, true);
        when(orderService.getAllOrders(0, 5, "totalAmount", "asc")).thenReturn(responseDto);

        ResponseEntity<OrderResponse> response = orderController.getAllOrders(0, 5, "totalAmount", "asc");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void getOrdersByUser_ShouldReturnOrders() {
        when(orderService.getOrdersByUser("email@test.com")).thenReturn(List.of(orderDTO));

        ResponseEntity<List<OrderDTO>> response = orderController.getOrdersByUser("email@test.com");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getOrderByUser_ShouldReturnOrder() {
        when(orderService.getOrder("email@test.com", 1L)).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.getOrderByUser("email@test.com", 1L);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() {
        when(orderService.updateOrder("email@test.com", 1L, "DELIVERED")).thenReturn(orderDTO);

        ResponseEntity<OrderDTO> response = orderController.updateOrderByUser("email@test.com", 1L, "DELIVERED");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDTO, response.getBody());
    }
}
