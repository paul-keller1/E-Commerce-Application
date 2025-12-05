package com.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.app.model.Cart;
import com.app.model.CartItem;
import com.app.model.Order;
import com.app.model.OrderItem;
import com.app.model.Payment;
import com.app.model.Product;
import com.app.exceptions.APIException;
import com.app.exceptions.ResourceNotFoundException;
import com.app.payloads.OrderDTO;
import com.app.payloads.OrderResponse;
import com.app.repositories.CartItemRepo;
import com.app.repositories.CartRepo;
import com.app.repositories.OrderItemRepo;
import com.app.repositories.OrderRepo;
import com.app.repositories.PaymentRepo;
import com.app.repositories.UserRepo;

public class OrderServiceImplTest {

    private AutoCloseable autoCloseable;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private OrderItemRepo orderItemRepo;

    @Mock
    private CartItemRepo cartItemRepo;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private OrderServiceImpl orderService;

    private Cart cart;
    private Cart emptyCart;
    private CartItem cartItem;
    private Product product;
    private Order order;
    private String emailId;
    private Long cartId;
    private Long orderId;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);

        emailId = "test@test.com";

        cartId = 1L;
        cart = new Cart();
        cart.setCartId(cartId);
        cart.setTotalPrice(200.0);

        emptyCart = new Cart();
        emptyCart.setCartId(2L);
        emptyCart.setTotalPrice(0.0);
        emptyCart.setCartItems(Collections.emptyList());

        product = new Product();
        product.setProductId(10L);
        product.setProductName("Test Product");
        product.setSpecialPrice(100.0);
        product.setDiscount(10);
        product.setQuantity(10);

        cartItem = new CartItem();
        cartItem.setCartItemId(100L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setProductPrice(100.0);
        cartItem.setDiscount(10);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        orderId = 5L;
        order = new Order();
        order.setOrderId(orderId);
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(300.0);
        order.setOrderStatus("Order Accepted !");
    }

    @AfterEach
    void tearDown() {
        try {
            autoCloseable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ---------------------------------------------------------
    // 1. placeOrder
    // ---------------------------------------------------------

    @Test
    void testPlaceOrder_Success() {
        int initialProductQuantity = product.getQuantity();

        when(cartRepo.findCartByEmailAndCartId(emailId, cart.getCartId())).thenReturn(cart);

        when(paymentRepo.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(1L);
            return payment;
        });

        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(orderId);
            return o;
        });

        when(orderItemRepo.saveAll(anyList())).thenAnswer(invocation -> {
            List<OrderItem> items = invocation.getArgument(0);
            long id = 1L;
            for (OrderItem item : items) {
                item.setOrderItemId(id++);
            }
            return items;
        });

        OrderDTO result = orderService.placeOrder(emailId, cart.getCartId(), "CARD");

        assertEquals(emailId, result.getEmail());
        assertEquals(cart.getTotalPrice(), result.getTotalAmount());
        assertEquals(1, result.getOrderItems().size());

        assertEquals(initialProductQuantity - cartItem.getQuantity(), product.getQuantity());

        verify(cartService, times(1))
                .deleteProductFromCart(cart.getCartId(), product.getProductId());

        verify(paymentRepo, times(1)).save(any(Payment.class));
        verify(orderRepo, times(1)).save(any(Order.class));
        verify(orderItemRepo, times(1)).saveAll(anyList());
    }

    @Test
    void testPlaceOrder_CartNotFound() {
        when(cartRepo.findCartByEmailAndCartId(emailId, cartId)).thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.placeOrder(emailId, cartId, "CARD")
        );
    }

    @Test
    void testPlaceOrder_EmptyCart() {
        when(cartRepo.findCartByEmailAndCartId(emailId, emptyCart.getCartId())).thenReturn(emptyCart);

        APIException ex = assertThrows(
                APIException.class,
                () -> orderService.placeOrder(emailId, emptyCart.getCartId(), "CARD")
        );

        assertEquals("Cart is empty", ex.getMessage());
        verify(orderItemRepo, never()).saveAll(anyList());
    }

    // ---------------------------------------------------------
    // 2. getOrdersByUser
    // ---------------------------------------------------------

    @Test
    void testGetOrdersByUser_ReturnsOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        when(orderRepo.findAllByEmail(emailId)).thenReturn(orders);

        List<OrderDTO> result = orderService.getOrdersByUser(emailId);

        assertEquals(1, result.size());
        assertEquals(order.getEmail(), result.get(0).getEmail());
    }

    @Test
    void testGetOrdersByUser_NoOrders() {
        when(orderRepo.findAllByEmail(emailId)).thenReturn(Collections.emptyList());

        APIException ex = assertThrows(
                APIException.class,
                () -> orderService.getOrdersByUser(emailId)
        );

        String expected = "No orders placed yet by the user with email: " + emailId;
        assertEquals(expected, ex.getMessage());
    }

    // ---------------------------------------------------------
    // 3. getOrder
    // ---------------------------------------------------------

    @Test
    void testGetOrder_Success() {
        when(orderRepo.findOrderByEmailAndOrderId(emailId, order.getOrderId())).thenReturn(order);

        OrderDTO result = orderService.getOrder(emailId, order.getOrderId());

        assertEquals(order.getOrderId(), result.getOrderId());
        assertEquals(order.getEmail(), result.getEmail());
    }

    @Test
    void testGetOrder_NotFound() {
        when(orderRepo.findOrderByEmailAndOrderId(emailId, orderId)).thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrder(emailId, orderId)
        );
    }

    // ---------------------------------------------------------
    // 4. getAllOrders
    // ---------------------------------------------------------

    @Test
    void testGetAllOrders_ReturnsPagedOrders_SortAsc() {
        List<Order> orders = new ArrayList<>();
        orders.add(order);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("orderId").ascending());
        Page<Order> page = new PageImpl<>(orders, pageRequest, orders.size());

        when(orderRepo.findAll(any(Pageable.class))).thenReturn(page);

        OrderResponse response = orderService.getAllOrders(0, 10, "orderId", "asc");

        assertEquals(1, response.getContent().size());
        assertEquals(order.getOrderId(), response.getContent().get(0).getOrderId());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(page.getTotalElements(), response.getTotalElements());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.isLast(), response.isLastPage());
    }

    @Test
    void testGetAllOrders_NoOrders_SortDesc() {
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("orderDate").descending());
        Page<Order> emptyPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        when(orderRepo.findAll(any(Pageable.class))).thenReturn(emptyPage);

        APIException ex = assertThrows(
                APIException.class,
                () -> orderService.getAllOrders(0, 5, "orderDate", "desc")
        );

        String expected = "No orders placed yet by the users";
        assertEquals(expected, ex.getMessage());
    }

    // ---------------------------------------------------------
    // 5. updateOrder
    // ---------------------------------------------------------

    @Test
    void testUpdateOrder_Success() {
        order.setOrderStatus("OLD_STATUS");

        when(orderRepo.findOrderByEmailAndOrderId(emailId, order.getOrderId())).thenReturn(order);

        String newStatus = "SHIPPED";
        OrderDTO result = orderService.updateOrder(emailId, order.getOrderId(), newStatus);

        assertEquals(newStatus, order.getOrderStatus());
        assertEquals(newStatus, result.getOrderStatus());
    }

    @Test
    void testUpdateOrder_OrderNotFound() {
        when(orderRepo.findOrderByEmailAndOrderId(emailId, orderId)).thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateOrder(emailId, orderId, "ANY_STATUS")
        );
    }
}
