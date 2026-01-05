package com.app.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.dto.OrderDTO;
import com.app.dto.OrderItemDTO;
import com.app.dto.OrderResponse;
import com.app.exception.APIException;
import com.app.model.Cart;
import com.app.model.CartItem;
import com.app.model.Order;
import com.app.model.OrderItem;
import com.app.model.Payment;
import com.app.model.Product;
import com.app.repository.CartItemRepo;
import com.app.repository.CartRepo;
import com.app.repository.OrderItemRepo;
import com.app.repository.OrderRepo;
import com.app.repository.PaymentRepo;
import com.app.repository.UserRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private CartRepo cartRepo;

	@Autowired
	private OrderRepo orderRepo;

	@Autowired
	private PaymentRepo paymentRepo;

	@Autowired
	private OrderItemRepo orderItemRepo;

	@Autowired
	private CartItemRepo cartItemRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private ModelMapper modelMapper;

	/*@
	  private invariant userRepo != null;
	  private invariant cartRepo != null;
	  private invariant orderRepo != null;
	  private invariant paymentRepo != null;
	  private invariant orderItemRepo != null;
	  private invariant cartItemRepo != null;
	  private invariant userService != null;
	  private invariant cartService != null;
	  private invariant modelMapper != null;
	@*/

	@Override
	public OrderDTO placeOrder(String emailId, Long cartId, String paymentMethod) {

		Cart cart = cartRepo.findCartByEmailAndCartId(emailId, cartId);

		if (cart == null) {
			throw new APIException("Cart not found with cartId: " + cartId);
		}

		Order order = new Order();
		order.setEmail(emailId);
		order.setOrderDate(LocalDate.now());
		order.setTotalAmount(cart.getTotalPrice());
		order.setOrderStatus("Order Accepted !");

		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setPaymentMethod(paymentMethod);

		payment = paymentRepo.save(payment);

		order.setPayment(payment);

		Order savedOrder = orderRepo.save(order);

		List<CartItem> cartItems = cart.getCartItems();

		if (cartItems.size() == 0) {
			throw new APIException("Cart is empty");
		}

		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();

			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setOrderedProductPrice(cartItem.getProductPrice());
			orderItem.setOrder(savedOrder);

			orderItems.add(orderItem);
		}

		orderItems = orderItemRepo.saveAll(orderItems);

		cart.getCartItems().forEach(item -> {
			int quantity = item.getQuantity();

			Product product = item.getProduct();

			cartService.deleteProductFromCart(cartId, item.getProduct().getProductId());

			product.setQuantity(product.getQuantity() - quantity);
		});

		OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

		orderItems.forEach(item ->
				orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

		return orderDTO;
	}

	@Override
	public OrderDTO getOrder(String emailId, Long orderId) {

		Order order = orderRepo.findOrderByEmailAndOrderId(emailId, orderId);

		if (order == null) {
			throw new APIException("Order not found with orderId: " + orderId);
		}

		return modelMapper.map(order, OrderDTO.class);
	}

	@Override
	public List<OrderDTO> getOrdersByUser(String emailId) {

		List<Order> orders = orderRepo.findAllByEmail(emailId);

		List<OrderDTO> orderDTOs = orders.stream()
				.map(order -> modelMapper.map(order, OrderDTO.class))
				.collect(Collectors.toList());

		if (orderDTOs.size() == 0) {
			throw new APIException("No orders placed yet by the user with email: " + emailId);
		}

		return orderDTOs;
	}

	@Override
	public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

		Sort sortByAndOrder;

		if (sortOrder.equalsIgnoreCase("asc")) {
			sortByAndOrder = Sort.by(sortBy).ascending();
		} else if (sortOrder.equalsIgnoreCase("desc")) {
			sortByAndOrder = Sort.by(sortBy).descending();
		} else {
			throw new APIException("Invalid sort order: " + sortOrder + ". Allowed values: asc, desc");
		}

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Order> pageOrders = orderRepo.findAll(pageDetails);

		List<Order> orders = pageOrders.getContent();

		List<OrderDTO> orderDTOs = orders.stream()
				.map(order -> modelMapper.map(order, OrderDTO.class))
				.collect(Collectors.toList());

		if (orderDTOs.size() == 0) {
			throw new APIException("No orders placed yet by the users");
		}

		OrderResponse orderResponse = new OrderResponse();
		orderResponse.setContent(orderDTOs);
		orderResponse.setPageNumber(pageOrders.getNumber());
		orderResponse.setPageSize(pageOrders.getSize());
		orderResponse.setTotalElements(pageOrders.getTotalElements());
		orderResponse.setTotalPages(pageOrders.getTotalPages());
		orderResponse.setLastPage(pageOrders.isLast());

		return orderResponse;
	}

	@Override
	public OrderDTO updateOrder(String emailId, Long orderId, String orderStatus) {

		Order order = orderRepo.findOrderByEmailAndOrderId(emailId, orderId);

		if (order == null) {
			throw new APIException("Order not found with orderId: " + orderId);
		}

		order.setOrderStatus(orderStatus);

		return modelMapper.map(order, OrderDTO.class);
	}
}
