package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.config.AppConstants;
import com.app.dto.OrderDTO;
import com.app.dto.OrderResponse;
import com.app.service.OrderService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class OrderController {

	@Autowired
	public OrderService orderService;

	/*@
	  private invariant orderService != null;
	@*/

	/*@
	  public normal_behavior
	  requires emailId != null;
	  requires cartId != null;
	  requires cartId.longValue() > 0;
	  requires paymentMethod != null;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 201;
	  ensures \result.getBody() != null;
	@*/
	@PostMapping("/user/user/{emailId}/carts/{cartId}/payments/{paymentMethod}/order")
	public ResponseEntity<OrderDTO> orderProducts(@PathVariable String emailId,
												  @PathVariable Long cartId,
												  @PathVariable String paymentMethod) {
		OrderDTO order = orderService.placeOrder(emailId, cartId, paymentMethod);
		return new ResponseEntity<OrderDTO>(order, HttpStatus.CREATED);
	}

	/*@
	  public normal_behavior
	  requires pageNumber != null;
	  requires pageSize != null;
	  requires sortBy != null;
	  requires sortOrder != null;
	  requires pageNumber.intValue() >= 0;
	  requires pageSize.intValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 302;
	  ensures \result.getBody() != null;
	@*/
	@GetMapping("/admin/orders")
	public ResponseEntity<OrderResponse> getAllOrders(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
		return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires emailId != null;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 302;
	  ensures \result.getBody() != null;
	@*/
	@GetMapping("user/user/{emailId}/orders")
	public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String emailId) {
		List<OrderDTO> orders = orderService.getOrdersByUser(emailId);
		return new ResponseEntity<List<OrderDTO>>(orders, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires emailId != null;
	  requires orderId != null;
	  requires orderId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 302;
	  ensures \result.getBody() != null;
	@*/
	@GetMapping("user/user/{emailId}/orders/{orderId}")
	public ResponseEntity<OrderDTO> getOrderByUser(@PathVariable String emailId, @PathVariable Long orderId) {
		OrderDTO order = orderService.getOrder(emailId, orderId);
		return new ResponseEntity<OrderDTO>(order, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires emailId != null;
	  requires orderId != null;
	  requires orderId.longValue() > 0;
	  requires orderStatus != null;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@PutMapping("admin/user/{emailId}/orders/{orderId}/orderStatus/{orderStatus}")
	public ResponseEntity<OrderDTO> updateOrderByUser(@PathVariable String emailId,
													  @PathVariable Long orderId,
													  @PathVariable String orderStatus) {
		OrderDTO order = orderService.updateOrder(emailId, orderId, orderStatus);
		return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);
	}
}
