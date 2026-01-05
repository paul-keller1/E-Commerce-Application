package com.app.config;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoConfig implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long userId;
	private String email;
	private String password;
	private Set<GrantedAuthority> authorities;


	/*@
	  public normal_behavior
	  requires user != null;
	@*/
	public UserInfoConfig(User user) {
		this.userId = user.getUserId();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getAuthority()))
				.collect(Collectors.toSet());
	}

	/*@
	also
	  public normal_behavior
	  ensures \result != null;
	@*/
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return (Collection<? extends GrantedAuthority>) authorities;
	}

	/*@
	also
	  public normal_behavior
	  ensures \result != null;
	@*/
	@Override
	public String getUsername() {
		return email;
	}

	/*@
	also
	  public normal_behavior
	  ensures \result == true;
	@*/
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/*@
	also
	  public normal_behavior
	  ensures \result == true;
	@*/
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/*@
	also
	  public normal_behavior
	  ensures \result == true;
	@*/
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/*@
	also
	  public normal_behavior
	  ensures \result == true;
	@*/
	@Override
	public boolean isEnabled() {
		return true;
	}

	/*@
	also
	  public normal_behavior
	  ensures true;
	@*/
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.getEmail());
		return hash;
	}

	/*@ //doesnt require obj to be != null by good practice

	also
	  public normal_behavior
	  ensures true;
	@*/
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof com.app.config.UserInfoConfig other)) {
			return false;
		}
		return Objects.equals(this.getEmail(), other.getEmail());
	}

	/*@
	also
	  public normal_behavior
	  ensures \result != null;
	@*/
	@Override
	public String toString() {
		return "id=" + email;
	}
}
