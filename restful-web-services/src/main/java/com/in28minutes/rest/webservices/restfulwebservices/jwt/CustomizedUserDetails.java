package com.in28minutes.rest.webservices.restfulwebservices.jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.in28minutes.rest.webservices.restfulwebservices.user.Role;
import com.in28minutes.rest.webservices.restfulwebservices.user.User;

public class CustomizedUserDetails implements UserDetails{


	private static final long serialVersionUID = 1234234234232L;
	
	private String username;
	private String password;
	private List<GrantedAuthority> roles ;
	private boolean isEnabled;
	
	public CustomizedUserDetails(User user) {
		username = user.getUsername();
		password = user.getPassword();
		roles = user.getRoles().stream().map(Role::getRoleName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		isEnabled = user.getEnabled();
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

}
