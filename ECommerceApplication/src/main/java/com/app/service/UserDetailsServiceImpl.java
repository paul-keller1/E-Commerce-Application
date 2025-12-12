package com.app.service;

import java.util.Optional;

import com.app.exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.config.UserInfoConfig;
import com.app.model.User;
import com.app.repository.UserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;


	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepo.findByEmail(email);
		
		return user.map(UserInfoConfig::new).orElseThrow(() -> new APIException("User with email " + email + " not found!!!"));
	}
}