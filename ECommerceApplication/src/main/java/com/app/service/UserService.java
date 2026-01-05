package com.app.service;

import com.app.dto.UserCreateDTO;
import com.app.dto.UserDTO;
import com.app.dto.UserResponse;

public interface UserService {

	/*@
		public normal_behavior
		  requires userCreateDTO != null;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires userCreateDTO != null;
		  signals (APIException e) true;
	@*/
	UserDTO registerUser(UserCreateDTO userDTO);


	/*@
		public normal_behavior
		  requires userCreateDTO != null;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires userCreateDTO != null;
		  signals (APIException e) true;

	@*/
	UserDTO registerAdmin(UserCreateDTO userDTO);


	/*@
		public normal_behavior
		  requires pageNumber != null;
		  requires pageSize != null;
		  requires sortBy != null;
		  requires sortOrder != null;
		  requires pageSize.intValue() > 0;
		  requires pageNumber.intValue() >= 0;
		  ensures \result != null;



		also


		public exceptional_behavior
		  requires pageNumber != null;
		  requires pageSize != null;
		  requires sortBy != null;
		  requires sortOrder != null;
		  requires pageSize.intValue() > 0;
		  requires pageNumber.intValue() >= 0;
		  signals (APIException e) true;
	@*/
	UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);


	/*@
		public normal_behavior
		  requires userId != null;
		  ensures \result != null;


		also


		public exceptional_behavior
		  requires userId != null;
		  signals (ResourceNotFoundException e) true;

	@*/
	UserDTO getUserById(Long userId);


	/*@
		public normal_behavior
		  requires userId != null;
		  requires userDTO != null;
		  requires userDTO.getPassword() != null;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires userId != null;
		  requires userDTO != null;
		  requires userDTO.getPassword() != null;
		  signals (ResourceNotFoundException e) true;


	@*/
	UserDTO updateUser(Long userId, UserDTO userDTO);



	/*@
		public normal_behavior
		  requires userId != null;
		  ensures \result != null;

		also


		public exceptional_behavior
		  requires userId != null;
		  signals (ResourceNotFoundException e) true;
	@*/
	String deleteUser(Long userId);

}
