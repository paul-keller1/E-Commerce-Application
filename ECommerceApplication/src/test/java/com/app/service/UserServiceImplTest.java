package com.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.app.model.*;
import com.app.dto.UserCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.exception.APIException;
import com.app.exception.ResourceNotFoundException;
import com.app.dto.AddressDTO;

import com.app.dto.UserDTO;
import com.app.dto.UserResponse;
import com.app.repository.AddressRepo;
import com.app.repository.UserRepo;
import org.springframework.test.util.ReflectionTestUtils;

public class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;



    @Mock
    private AddressRepo addressRepo;

    @Mock
    private CartService cartService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User userWithoutAddress;
    private Cart cart;
    private CartItem cartItem;
    private Product product;
    private Address address;
    private Role role;
    private Long userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = 1L;

        // Address
        address = new Address();
        address.setAddressId(10L);
        address.setCountry("Country");
        address.setState("State");
        address.setCity("City");
        address.setPincode("12345");
        address.setStreet("Main Street");
        address.setBuildingName("Building");

        // Product
        product = new Product();
        product.setProductId(100L);
        product.setName("Product");
        product.setSpecialPrice(200.0);
        product.setDiscount(5);
        product.setQuantity(10);

        // CartItem
        cartItem = new CartItem();
        cartItem.setCartItemId(200L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setProductPrice(product.getSpecialPrice());

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        // Cart
        cart = new Cart();
        cart.setCartId(300L);
        cart.setTotalPrice(400.0);
        cart.setCartItems(cartItems);
        cartItem.setCart(cart);

        // User with address
        user = new User();
        user.setUserId(userId);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("user@test.com");
        user.setPassword("password");
        user.setMobileNumber("1234567890");
        user.setCart(cart);
        user.setAddresses(List.of(address));
        cart.setUser(user);

        // User without address
        userWithoutAddress = new User();
        userWithoutAddress.setUserId(2L);
        userWithoutAddress.setFirstName("No");
        userWithoutAddress.setLastName("Address");
        userWithoutAddress.setEmail("noaddress@test.com");
        userWithoutAddress.setPassword("password2");
        userWithoutAddress.setMobileNumber("0987654321");
        Cart cart2 = new Cart();
        cart2.setCartId(400L);
        cart2.setTotalPrice(0.0);
        cart2.setCartItems(Collections.emptyList());
        cart2.setUser(userWithoutAddress);
        userWithoutAddress.setCart(cart2);
        userWithoutAddress.setAddresses(new ArrayList<>());


    }

    // ---------------------------------------------------------
    // 1. registerUser
    // ---------------------------------------------------------

    @Test
    void testRegisterUser_Success_NewAddressCreated() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName("New");
        userCreateDTO.setLastName("User");
        userCreateDTO.setEmail("newuser@test.com");
        userCreateDTO.setMobileNumber("5555555555");
        userCreateDTO.setPassword("secret");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry("Country");
        addressDTO.setState("State");
        addressDTO.setCity("City");
        addressDTO.setPincode("12345");
        addressDTO.setStreet("Main Street");
        addressDTO.setBuildingName("Building");
        userCreateDTO.setAddress(addressDTO);

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                addressDTO.getCountry(),
                addressDTO.getState(),
                addressDTO.getCity(),
                addressDTO.getPincode(),
                addressDTO.getStreet(),
                addressDTO.getBuildingName()
        )).thenReturn(null);

        when(addressRepo.save(any(Address.class))).thenAnswer(invocation -> {
            Address a = invocation.getArgument(0);
            a.setAddressId(50L);
            return a;
        });

        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(10L);
            return u;
        });

        UserDTO result = userService.registerUser(userCreateDTO);

        assertEquals(userCreateDTO.getEmail(), result.getEmail());
        assertNotNull(result.getAddress());
        assertEquals(userCreateDTO.getAddress().getCity(), result.getAddress().getCity());
    }


    @Test
    void testRegisterUser_Success_AddressAlreadyExists() {
        UserCreateDTO userDTO = new UserCreateDTO();
        userDTO.setFirstName("Existing");
        userDTO.setLastName("AddressUser");
        userDTO.setEmail("existing@test.com");
        userDTO.setMobileNumber("5550000000");
        userDTO.setPassword("pass");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry(address.getCountry());
        addressDTO.setState(address.getState());
        addressDTO.setCity(address.getCity());
        addressDTO.setPincode(address.getPincode());
        addressDTO.setStreet(address.getStreet());
        addressDTO.setBuildingName(address.getBuildingName());
        userDTO.setAddress(addressDTO);

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                addressDTO.getCountry(),
                addressDTO.getState(),
                addressDTO.getCity(),
                addressDTO.getPincode(),
                addressDTO.getStreet(),
                addressDTO.getBuildingName()
        )).thenReturn(address);

        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(20L);
            return u;
        });

        UserDTO result = userService.registerUser(userDTO);

        assertEquals(userDTO.getEmail(), result.getEmail());
        assertNotNull(result.getAddress());
        assertEquals(address.getCity(), result.getAddress().getCity());
    }

    @Test
    void testRegisterUser_UserAlreadyExists_ThrowsAPIException() {
        UserCreateDTO userDTO = new UserCreateDTO();
        userDTO.setFirstName("Duplicate");
        userDTO.setLastName("User");
        userDTO.setEmail("user@test.com");
        userDTO.setMobileNumber("1231231234");
        userDTO.setPassword("secret");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry(address.getCountry());
        addressDTO.setState(address.getState());
        addressDTO.setCity(address.getCity());
        addressDTO.setPincode(address.getPincode());
        addressDTO.setStreet(address.getStreet());
        addressDTO.setBuildingName(address.getBuildingName());
        userDTO.setAddress(addressDTO);

        when(userRepo.getUserByEmail(any())).thenReturn(Optional.of(user));


        APIException ex = assertThrows(
                APIException.class,
                () -> userService.registerUser(userDTO)
        );

    }

    @Test
    void testRegisterAdmin_AssignsAdminRole() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName("Admin");
        userCreateDTO.setLastName("User");
        userCreateDTO.setEmail("admin@test.com");
        userCreateDTO.setMobileNumber("1111111111");
        userCreateDTO.setPassword("secret");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry("Country");
        addressDTO.setState("State");
        addressDTO.setCity("City");
        addressDTO.setPincode("12345");
        addressDTO.setStreet("Main Street");
        addressDTO.setBuildingName("Building");
        userCreateDTO.setAddress(addressDTO);

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                addressDTO.getCountry(),
                addressDTO.getState(),
                addressDTO.getCity(),
                addressDTO.getPincode(),
                addressDTO.getStreet(),
                addressDTO.getBuildingName()
        )).thenReturn(null);

        when(addressRepo.save(any(Address.class))).thenAnswer(invocation -> {
            Address a = invocation.getArgument(0);
            a.setAddressId(60L);
            return a;
        });

        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(30L);
            return u;
        });

        UserDTO result = userService.registerAdmin(userCreateDTO);

        assertEquals(userCreateDTO.getEmail(), result.getEmail());
        assertTrue(result.getRoles().contains(Role.ADMIN));
        assertTrue(result.getRoles().contains(Role.USER));
    }

    @Test
    void registerUser_ShouldWrapDataIntegrityViolation() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setFirstName("Data");
        userCreateDTO.setLastName("Violation");
        userCreateDTO.setEmail("data@test.com");
        userCreateDTO.setMobileNumber("1231231234");
        userCreateDTO.setPassword("pwd");
        AddressDTO addressDTO = new AddressDTO(1L, "Street", "Building", "City", "State", "Country", "12345");
        userCreateDTO.setAddress(addressDTO);

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(address);
        when(userRepo.save(any(User.class))).thenThrow(new DataIntegrityViolationException("dup"));

        APIException apiException = assertThrows(APIException.class, () -> userService.registerUser(userCreateDTO));
        assertTrue(apiException.getMessage().contains("data@test.com"));
    }

    @Test
    void registerUser_NullInputStillWrappedInApiException() {
        doThrow(new DataIntegrityViolationException("fail"))
                .when(modelMapper).map(null, User.class);

        APIException ex = assertThrows(APIException.class, () -> userService.registerUser(null));
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void toUserShouldHandleNullAddress() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setFirstName("NoAddr");
        dto.setLastName("User");
        dto.setEmail("noaddr@test.com");
        dto.setPassword("pwd");
        dto.setMobileNumber("123");
        dto.setAddress(null);

        User userResult = ReflectionTestUtils.invokeMethod(userService, "toUser", dto, Set.of(Role.USER));

        assertNotNull(userResult);
        assertTrue(userResult.getAddresses() == null || userResult.getAddresses().isEmpty());
        assertNull(userResult.getCart());
    }

    // ---------------------------------------------------------
    // 2. getAllUsers
    // ---------------------------------------------------------

    @Test
    void testGetAllUsers_ReturnsUsers_SortAsc_WithAddress() {
        List<User> users = List.of(user);

        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by("userId").ascending());
        Page<User> pageUsers = new PageImpl<>(users, pageRequest, users.size());

        when(userRepo.findAll(any(Pageable.class))).thenReturn(pageUsers);

        UserResponse response = userService.getAllUsers(0, 5, "userId", "asc");

        assertEquals(users.size(), response.getContent().size());
        assertEquals(user.getUserId(), response.getContent().get(0).getUserId());
        assertNotNull(response.getContent().get(0).getAddress());
        assertEquals(pageUsers.getNumber(), response.getPageNumber());
        assertEquals(pageUsers.getSize(), response.getPageSize());
        assertEquals(pageUsers.getTotalElements(), response.getTotalElements());
        assertEquals(pageUsers.getTotalPages(), response.getTotalPages());
        assertEquals(pageUsers.isLast(), response.isLastPage());
    }

    @Test
    void testGetAllUsers_ReturnsUsers_SortDesc_NoAddress() {
        List<User> users = List.of(userWithoutAddress);

        PageRequest pageRequest = PageRequest.of(1, 3, Sort.by("email").descending());
        Page<User> pageUsers = new PageImpl<>(users, pageRequest, users.size());

        when(userRepo.findAll(any(Pageable.class))).thenReturn(pageUsers);

        UserResponse response = userService.getAllUsers(1, 3, "email", "desc");

        assertEquals(users.size(), response.getContent().size());
        assertEquals(userWithoutAddress.getUserId(), response.getContent().get(0).getUserId());
        assertNull(response.getContent().get(0).getAddress()); // branch when user.getAddresses().size() == 0
    }

    @Test
    void testGetAllUsers_NoUsers_ThrowsAPIException() {
        List<User> users = Collections.emptyList();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("userId").ascending());
        Page<User> pageUsers = new PageImpl<>(users, pageRequest, users.size());

        when(userRepo.findAll(any(Pageable.class))).thenReturn(pageUsers);

        APIException ex = assertThrows(
                APIException.class,
                () -> userService.getAllUsers(0, 10, "userId", "asc")
        );

        assertEquals("No User exists !!!", ex.getMessage());
    }

    // ---------------------------------------------------------
    // 3. getUserById
    // ---------------------------------------------------------

    @Test
    void testGetUserById_Success() {
        when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(user.getUserId());

        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(user.getEmail(), result.getEmail());
        assertNotNull(result.getAddress());
        assertNotNull(result.getCart());
        assertEquals(user.getCart().getCartId(), result.getCart().getCartId());
    }

    @Test
    void testGetUserById_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(userId)
        );
    }

    // ---------------------------------------------------------
    // 4. updateUser
    // ---------------------------------------------------------

    @Test
    void testUpdateUser_Success_NoAddressUpdate() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("UpdatedFirst");
        userDTO.setLastName("UpdatedLast");
        userDTO.setEmail("updated@test.com");
        userDTO.setMobileNumber("1111111111");
        userDTO.setPassword("newpass");
        userDTO.setAddress(null);

        when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPass");

        UserDTO result = userService.updateUser(user.getUserId(), userDTO);

        assertEquals(userDTO.getFirstName(), result.getFirstName());
        assertEquals(userDTO.getLastName(), result.getLastName());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals(userDTO.getMobileNumber(), result.getMobileNumber());
        assertEquals("encodedPass", user.getPassword());
        assertNotNull(result.getAddress());
        assertEquals(user.getAddresses().get(0).getCity(), result.getAddress().getCity());
    }

    @Test
    void testUpdateUser_Success_ExistingAddressUsed_NoNewSave() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("UpdatedExistingAddress");
        userDTO.setLastName("User");
        userDTO.setEmail("existingaddressupdated@test.com");
        userDTO.setMobileNumber("2222222222");
        userDTO.setPassword("pwd2");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry(address.getCountry());
        addressDTO.setState(address.getState());
        addressDTO.setCity(address.getCity());
        addressDTO.setPincode(address.getPincode());
        addressDTO.setStreet(address.getStreet());
        addressDTO.setBuildingName(address.getBuildingName());
        userDTO.setAddress(addressDTO);

        when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encoded2");
        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                addressDTO.getCountry(),
                addressDTO.getState(),
                addressDTO.getCity(),
                addressDTO.getPincode(),
                addressDTO.getStreet(),
                addressDTO.getBuildingName()
        )).thenReturn(address);

        UserDTO result = userService.updateUser(user.getUserId(), userDTO);

        assertEquals(userDTO.getFirstName(), result.getFirstName());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals("encoded2", user.getPassword());
        assertEquals(address.getCity(), result.getAddress().getCity());

        // No new address saved in this branch
        verify(addressRepo, never()).save(any(Address.class));
    }

    @Test
    void testUpdateUser_Success_NewAddressCreated() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("NewAddress");
        userDTO.setLastName("User");
        userDTO.setEmail("newaddress@test.com");
        userDTO.setMobileNumber("3333333333");
        userDTO.setPassword("pwd3");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCountry("NewCountry");
        addressDTO.setState("NewState");
        addressDTO.setCity("NewCity");
        addressDTO.setPincode("99999");
        addressDTO.setStreet("NewStreet");
        addressDTO.setBuildingName("NewBuilding");
        userDTO.setAddress(addressDTO);

        when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encoded3");

        when(addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
                addressDTO.getCountry(),
                addressDTO.getState(),
                addressDTO.getCity(),
                addressDTO.getPincode(),
                addressDTO.getStreet(),
                addressDTO.getBuildingName()
        )).thenReturn(null);

        when(addressRepo.save(any(Address.class))).thenAnswer(invocation -> {
            Address a = invocation.getArgument(0);
            a.setAddressId(999L);
            return a;
        });

        UserDTO result = userService.updateUser(user.getUserId(), userDTO);

        assertEquals(userDTO.getFirstName(), result.getFirstName());
        assertEquals(userDTO.getEmail(), result.getEmail());
        assertEquals("encoded3", user.getPassword());
        assertEquals(addressDTO.getCity(), result.getAddress().getCity());

        verify(addressRepo, times(1)).save(any(Address.class));
    }

    @Test
    void testUpdateUser_UserNotFound_ThrowsResourceNotFoundException() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("X");
        userDTO.setLastName("Y");
        userDTO.setEmail("xy@test.com");
        userDTO.setMobileNumber("0000000000");
        userDTO.setPassword("pwd");

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(userId, userDTO)
        );
    }

    // ---------------------------------------------------------
    // 5. deleteUser
    // ---------------------------------------------------------

    @Test
    void testDeleteUser_Success() {
        when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(cartService.deleteProductFromCart(
                user.getCart().getCartId(),
                product.getProductId()
        )).thenReturn("removed");

        String message = userService.deleteUser(user.getUserId());

        String expectedMessage = "User with userId " + user.getUserId() + " deleted successfully!!!";
        assertEquals(expectedMessage, message);

        verify(cartService, times(1))
                .deleteProductFromCart(user.getCart().getCartId(), product.getProductId());
        verify(userRepo, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(userId)
        );
    }
}
