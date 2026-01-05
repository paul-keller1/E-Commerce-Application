package com.app.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.app.dto.UserCreateDTO;
import com.app.exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.app.exception.UserNotFoundException;
import com.app.dto.LoginCredentials;
import com.app.dto.UserDTO;
import com.app.security.JWTService;
import com.app.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private JWTService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${admin.register.secret}")
	private String adminRegisterSecret;

	/*@
	  private invariant userService != null;
	  private invariant jwtService != null;
	  private invariant authenticationManager != null;
	  private invariant passwordEncoder != null;
	@*/

	/*@
	  public normal_behavior
	  requires user != null;
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 201;
	  ensures \result.getBody() != null;
	@*/
	@PostMapping("/register/user")
	public ResponseEntity<Map<String, Object>> registerHandler(@Valid @RequestBody UserCreateDTO user)
			throws UserNotFoundException {

		String encodedPass = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPass);

		UserDTO userDTO = userService.registerUser(user);

		String token = jwtService.generateToken(userDTO.getEmail());
		Map<String, Object> response = new HashMap<>();
		response.put("user", userDTO);
		response.put("token", token);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/*@
	  public normal_behavior
	  requires secret != null;
	  requires user != null;
	  requires adminRegisterSecret != null;
	  requires !adminRegisterSecret.isBlank();
	  requires adminRegisterSecret.equals(secret);
	  ensures \result != null;
	  ensures \result.getStatusCodeValue() == 201;
	  ensures \result.getBody() != null;
	@*/
	@PostMapping("/register/admin")
	public ResponseEntity<Map<String, Object>> registerAdminHandler(
			@RequestParam("secret") String secret,
			@Valid @RequestBody UserCreateDTO user) {

		if (adminRegisterSecret == null || adminRegisterSecret.isBlank() || !adminRegisterSecret.equals(secret)) {
			throw new APIException("Invalid admin registration secret");
		}

		String encodedPass = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPass);

		UserDTO userDTO = userService.registerAdmin(user);

		String token = jwtService.generateToken(userDTO.getEmail());
		Map<String, Object> response = new HashMap<>();
		response.put("user", userDTO);
		response.put("token", token);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/*@
	  public normal_behavior
	  requires credentials != null;
	  ensures \result != null;
	@*/
	@PostMapping("/login")
	public Map<String, Object> loginHandler(@Valid @RequestBody LoginCredentials credentials) {

		UsernamePasswordAuthenticationToken authCredentials =
				new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());

		authenticationManager.authenticate(authCredentials);

		String token = jwtService.generateToken(credentials.getEmail());

		return Collections.singletonMap("jwt-token", token);
	}
}
