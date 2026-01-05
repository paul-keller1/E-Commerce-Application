package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.config.AppConstants;
import com.app.dto.UserDTO;
import com.app.dto.UserResponse;
import com.app.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class UserController {

	@Autowired
	private UserService userService;

	/*@
	  private invariant userService != null;
	@*/

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
	@GetMapping("/admin/users")
	public ResponseEntity<UserResponse> getUsers(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_USERS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

		UserResponse userResponse = userService.getAllUsers(pageNumber, pageSize, sortBy, sortOrder);
		return new ResponseEntity<UserResponse>(userResponse, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires userId != null;
	  requires userId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 302;
	  ensures \result.getBody() != null;
	@*/
	@GetMapping("/admin/users/{userId}")
	public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
		UserDTO user = userService.getUserById(userId);
		return new ResponseEntity<UserDTO>(user, HttpStatus.FOUND);
	}

	/*@
	  public normal_behavior
	  requires userDTO != null;
	  requires userId != null;
	  requires userId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@PutMapping("/admin/users/{userId}")
	public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
		UserDTO updatedUser = userService.updateUser(userId, userDTO);
		return new ResponseEntity<UserDTO>(updatedUser, HttpStatus.OK);
	}

	/*@
	  public normal_behavior
	  requires userId != null;
	  requires userId.longValue() > 0;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 200;
	  ensures \result.getBody() != null;
	@*/
	@DeleteMapping("/admin/users/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
		String status = userService.deleteUser(userId);
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
}
