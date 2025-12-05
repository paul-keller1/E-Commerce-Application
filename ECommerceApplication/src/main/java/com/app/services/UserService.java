package com.app.services;

import com.app.payloads.UserCreateDTO;
import com.app.payloads.UserDTO;
import com.app.payloads.UserResponse;

public interface UserService {
	UserDTO registerUser(UserCreateDTO userDTO);
	
	UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	UserDTO getUserById(Long userId);
	
	UserDTO updateUser(Long userId, UserDTO userDTO);
	
	String deleteUser(Long userId);
}
