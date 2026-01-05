package com.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.config.UserInfoConfig;
import com.app.exception.APIException;
import com.app.model.User;
import com.app.repository.UserRepo;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	/*@
	  private invariant userRepo != null;
	@*/

	/*@
		also
		public normal_behavior
		  requires email != null;
		  ensures \result != null;


		also

		public exceptional_behavior
		  requires email != null;
		  signals (APIException e) true;
		  signals (UsernameNotFoundException e) true;
	@*/
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Optional<User> user = userRepo.findByEmail(email);

		return user.map(UserInfoConfig::new)
				.orElseThrow(() ->
						new APIException("User with email " + email + " not found!!!"));
	}
}
