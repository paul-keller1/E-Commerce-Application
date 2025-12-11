package com.app.service;

import com.app.dto.UserCreateDTO;
import com.app.dto.UserDTO;
import com.app.dto.UserResponse;

public interface UserService {
	UserDTO registerUser(UserCreateDTO userDTO);
	
	UserDTO registerAdmin(UserCreateDTO userDTO);
	
	UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	UserDTO getUserById(Long userId);
	
	UserDTO updateUser(Long userId, UserDTO userDTO);
	
	String deleteUser(Long userId);

}
