package com.app.config;

import com.app.model.Role;
import com.app.model.User;
import com.app.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.security.JWTFilter;
import com.app.service.UserDetailsServiceImpl;

import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JWTFilter jwtFilter;

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	/*@
	  private invariant jwtFilter != null;
	  private invariant userDetailsServiceImpl != null;
	@*/

	/*@
	  public normal_behavior
	  requires http != null;
	  ensures \result != null;

	  also


	  public exceptional_behavior
	  requires http != null;
	  signals (Exception e) true;
	@*/
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(AppConstants.PUBLIC_URLS).permitAll()
						.requestMatchers(AppConstants.USER_URLS).hasAnyAuthority("USER", "ADMIN")
						.requestMatchers(AppConstants.ADMIN_URLS).hasAuthority("ADMIN")
						.anyRequest().authenticated()
				)
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	/*@
	  public normal_behavior
	  ensures \result != null;
	@*/
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsServiceImpl);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	/*@
	  public normal_behavior
	  ensures \result != null;
	@*/
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*@
	  normal_behavior
	  requires userRepo != null;
	  requires encoder != null;
	  ensures \result != null;
	@*/
	@Bean
	CommandLineRunner seedUsers(UserRepo userRepo, PasswordEncoder encoder) {
		return args -> {
			System.out.println(">>> seeding users...");
			if (userRepo.findByEmail("admin@app.com").isEmpty()) {
				User u = new User();
				u.setEmail("admin@app.com");
				u.setPassword(encoder.encode("admin123"));
				u.setRoles(Set.of(Role.ADMIN));
				userRepo.save(u);
			}
		};
	}

	/*@
	  public normal_behavior
	  requires configuration != null;
	  ensures \result != null;

	  also


	  public exceptional_behavior
	  requires configuration != null;
	  signals (Exception e) true;

	@*/
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
