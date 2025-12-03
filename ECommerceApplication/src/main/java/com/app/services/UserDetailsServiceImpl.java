package com.app.services;

import java.util.Optional;

import com.app.exceptions.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.config.UserInfoConfig;
import com.app.entites.User;
import com.app.repositories.UserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;


	//TODO: maybe i should change sth about the way tokens return emails instead of usernames, loadUserByUsername is from a remote interface
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepo.findByEmail(email);
		
		return user.map(UserInfoConfig::new).orElseThrow(() -> new APIException("User with email" + email + "not found!!!"));
	}
}