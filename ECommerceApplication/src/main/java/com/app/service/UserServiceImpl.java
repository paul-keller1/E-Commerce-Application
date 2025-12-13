package com.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.app.model.*;
import com.app.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.exception.APIException;
import com.app.exception.ResourceNotFoundException;
import com.app.repository.AddressRepo;
import com.app.repository.UserRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;


	@Autowired
	private AddressRepo addressRepo;

	@Autowired
	private CartService cartService;

	@Autowired
	private PasswordEncoder passwordEncoder;



	@Autowired
	private ModelMapper modelMapper;

	private User toUser(UserCreateDTO dto, Set<Role> roles) {
		User user = modelMapper.map(dto, User.class);
		user.setRoles(roles);

		// Important: these you usually *override* server-side:
		user.setCart(null);              // cart is created in service, not from DTO

		// Address: entity has List<Address>, DTO has single AddressDTO
		if (dto.getAddress() != null) {
			Address address = modelMapper.map(dto.getAddress(), Address.class);
			ArrayList<Address> list = new ArrayList<>();
			list.add(address);
			user.setAddresses(list);
		}

		return user;
	}


	@Override
	public UserDTO registerUser(UserCreateDTO userCreateDTO) {
		return registerWithRoles(userCreateDTO, Set.of(Role.USER));
	}

	@Override
	public UserDTO registerAdmin(UserCreateDTO userCreateDTO) {
		// Admins also get USER role for user-facing operations.
		return registerWithRoles(userCreateDTO, Set.of(Role.ADMIN, Role.USER));
	}

	private UserDTO registerWithRoles(UserCreateDTO userCreateDTO, Set<Role> roles) {
		try {
			User user = toUser(userCreateDTO, roles);


			Cart cart = new Cart();

			userRepo.getUserByEmail(user.getEmail()).ifPresent(userOld-> {
				throw new APIException("for this email there is already a user present" + userOld);
			});


			// owning side:
			cart.setUser(user);
			// inverse side:
			user.setCart(cart);

			String country = userCreateDTO.getAddress().getCountry();
			String state = userCreateDTO.getAddress().getState();
			String city = userCreateDTO.getAddress().getCity();
			String pincode = userCreateDTO.getAddress().getPincode();
			String street = userCreateDTO.getAddress().getStreet();
			String buildingName = userCreateDTO.getAddress().getBuildingName();

			Address address = addressRepo
					.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
							country, state, city, pincode, street, buildingName
					);

			if (address == null) {
				address = new Address(country, state, city, pincode, street, buildingName);
				address = addressRepo.save(address);
			}

			user.setAddresses(List.of(address));

			User registeredUser = userRepo.save(user);

			UserDTO userDTO = modelMapper.map(registeredUser, UserDTO.class);
			userDTO.setAddress(
					modelMapper.map(
							registeredUser.getAddresses().get(0),
							AddressDTO.class
					)
			);

			return userDTO;

		} catch (DataIntegrityViolationException e) {
			throw new APIException(
					"Data Integrity Violation for user with email: "
							+ (userCreateDTO != null ? userCreateDTO.getEmail() : "null")
							+ "; " + e.getMessage()
			);
		}
	}



	@Override
	public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		
		Page<User> pageUsers = userRepo.findAll(pageDetails);
		
		List<User> users = pageUsers.getContent();

		if (users.size() == 0) {
			throw new APIException("No User exists !!!");
		}

		List<UserDTO> userDTOs = users.stream().map(user -> {
			UserDTO dto = modelMapper.map(user, UserDTO.class);

			if (user.getAddresses().size() != 0) {
				dto.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
			}

			CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);

			List<ProductDTO> products = user.getCart().getCartItems().stream()
					.map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());

			dto.setCart(cart);

			dto.getCart().setProducts(products);

			return dto;

		}).collect(Collectors.toList());

		UserResponse userResponse = new UserResponse();
		
		userResponse.setContent(userDTOs);
		userResponse.setPageNumber(pageUsers.getNumber());
		userResponse.setPageSize(pageUsers.getSize());
		userResponse.setTotalElements(pageUsers.getTotalElements());
		userResponse.setTotalPages(pageUsers.getTotalPages());
		userResponse.setLastPage(pageUsers.isLast());
		
		return userResponse;
	}

	@Override
	public UserDTO getUserById(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		UserDTO userDTO = modelMapper.map(user, UserDTO.class);

		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

		CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);

		List<ProductDTO> products = user.getCart().getCartItems().stream()
				.map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());

		userDTO.setCart(cart);

		userDTO.getCart().setProducts(products);

		return userDTO;
	}

	@Override
	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		String encodedPass = passwordEncoder.encode(userDTO.getPassword());

		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setMobileNumber(userDTO.getMobileNumber());
		user.setEmail(userDTO.getEmail());
		user.setPassword(encodedPass);

		if (userDTO.getAddress() != null) {
			String country = userDTO.getAddress().getCountry();
			String state = userDTO.getAddress().getState();
			String city = userDTO.getAddress().getCity();
			String pincode = userDTO.getAddress().getPincode();
			String street = userDTO.getAddress().getStreet();
			String buildingName = userDTO.getAddress().getBuildingName();

			Address address = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(country, state,
					city, pincode, street, buildingName);

			if (address == null) {
				address = new Address(country, state, city, pincode, street, buildingName);

				address = addressRepo.save(address);

				user.setAddresses(List.of(address));
			}
		}

		userDTO = modelMapper.map(user, UserDTO.class);

		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

		CartDTO cart = modelMapper.map(user.getCart(), CartDTO.class);

		List<ProductDTO> products = user.getCart().getCartItems().stream()
				.map(item -> modelMapper.map(item.getProduct(), ProductDTO.class)).collect(Collectors.toList());

		userDTO.setCart(cart);

		userDTO.getCart().setProducts(products);

		return userDTO;
	}

	@Override
	public String deleteUser(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		List<CartItem> cartItems = user.getCart().getCartItems();
		Long cartId = user.getCart().getCartId();

		cartItems.forEach(item -> {

			Long productId = item.getProduct().getProductId();

			cartService.deleteProductFromCart(cartId, productId);
		});

		userRepo.delete(user);

		return "User with userId " + userId + " deleted successfully!!!";
	}

}
