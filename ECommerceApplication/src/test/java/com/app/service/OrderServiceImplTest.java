package com.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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
import com.app.exception.APIException;
import com.app.dto.OrderDTO;
import com.app.dto.OrderResponse;
import com.app.repository.CartItemRepo;
import com.app.repository.CartRepo;
import com.app.repository.OrderItemRepo;
import com.app.repository.OrderRepo;
import com.app.repository.PaymentRepo;
import com.app.repository.UserRepo;

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
        product.setName("Test Product");
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
    void testPlaceOrder_CartNotFound() {
        when(cartRepo.findCartByEmailAndCartId(emailId, cartId)).thenReturn(null);

        APIException ex = assertThrows(
                APIException.class,
                () -> orderService.placeOrder(emailId, cartId, "CARD")
        );

        assertEquals("Cart not found with cartId: " + cartId, ex.getMessage());
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

        APIException ex = assertThrows(
                APIException.class,
                () -> orderService.getOrder(emailId, orderId)
        );

        assertEquals("Order not found with orderId: " + orderId, ex.getMessage());
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
    void testPlaceOrder_Success() {
        int initialProductQuantity = product.getQuantity();

        when(cartRepo.findCartByEmailAndCartId(emailId, cart.getCartId())).thenReturn(cart);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<OrderItem>> orderItemsCaptor = ArgumentCaptor.forClass(List.class);

        when(paymentRepo.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        when(orderRepo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        when(orderItemRepo.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = orderService.placeOrder(emailId, cart.getCartId(), "CARD");

        verify(paymentRepo).save(paymentCaptor.capture());
        verify(orderRepo).save(orderCaptor.capture());
        verify(orderItemRepo).saveAll(orderItemsCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        Order savedOrder = orderCaptor.getValue();
        List<OrderItem> savedOrderItems = orderItemsCaptor.getValue();

        assertNotNull(savedPayment);
        assertNotNull(savedOrder);
        assertNotNull(savedOrderItems);
        assertEquals(1, savedOrderItems.size());


        assertEquals(LocalDate.now(), savedOrder.getOrderDate(),
                "Order date must be set to today's date");
        assertEquals("Order Accepted !", savedOrder.getOrderStatus(),
                "Order status must be set to the accepted status");

        assertEquals(emailId, savedOrder.getEmail());
        assertEquals(cart.getTotalPrice(), savedOrder.getTotalAmount(), 0.0001);


        OrderItem savedItem = savedOrderItems.get(0);
        assertEquals(cartItem.getDiscount(), savedItem.getDiscount(),
                "OrderItem.discount must be copied from CartItem");
        assertEquals(cartItem.getProductPrice(), savedItem.getOrderedProductPrice(), 0.0001,
                "OrderItem.orderedProductPrice must be copied from CartItem");

        assertSame(savedOrder, savedItem.getOrder(), "OrderItem.order must be set to saved order");
        assertSame(product, savedItem.getProduct(), "OrderItem.product must be copied from CartItem");
        assertEquals(cartItem.getQuantity(), savedItem.getQuantity(), "OrderItem.quantity must match cart item");

        assertSame(savedOrder, savedPayment.getOrder(), "Payment.order must be set");
        assertSame(savedPayment, savedOrder.getPayment(), "Order.payment must be set");
        assertEquals("CARD", savedPayment.getPaymentMethod());

        assertEquals(initialProductQuantity - cartItem.getQuantity(), product.getQuantity());
        verify(cartService).deleteProductFromCart(cart.getCartId(), product.getProductId());

        assertEquals(emailId, result.getEmail());
        assertEquals(cart.getTotalPrice(), result.getTotalAmount(), 0.0001);
        assertNotNull(result.getOrderItems());
        assertEquals(1, result.getOrderItems().size());
    }



    @Test
    void testUpdateOrder_Success() {
        String newStatus = "SHIPPED";

        when(orderRepo.findOrderByEmailAndOrderId(emailId, orderId)).thenReturn(order);

        when(orderRepo.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDTO result = orderService.updateOrder(emailId, orderId, newStatus);

        assertEquals(newStatus, order.getOrderStatus(), "Order status should be updated on entity");
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(emailId, result.getEmail());
        assertEquals(newStatus, result.getOrderStatus());

        verify(orderRepo).findOrderByEmailAndOrderId(emailId, orderId);


    }


    @Test
    void testUpdateOrder_OrderNotFound() {
        when(orderRepo.findOrderByEmailAndOrderId(emailId, orderId)).thenReturn(null);

        APIException ex = assertThrows(
                APIException.class,
                () -> orderService.updateOrder(emailId, orderId, "ANY_STATUS")
        );

        assertEquals("Order not found with orderId: " + orderId, ex.getMessage());
    }
}
