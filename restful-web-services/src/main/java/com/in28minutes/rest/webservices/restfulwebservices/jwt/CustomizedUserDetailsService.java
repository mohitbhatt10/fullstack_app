package com.in28minutes.rest.webservices.restfulwebservices.jwt;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.in28minutes.rest.webservices.restfulwebservices.user.User;
import com.in28minutes.rest.webservices.restfulwebservices.user.UserRepository;

@Component
public class CustomizedUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {	
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isPresent()) {
			return new CustomizedUserDetails(user.get());
		}
		else {
			throw new UsernameNotFoundException("No user found with username: "+username);
		}
	}
	

}
