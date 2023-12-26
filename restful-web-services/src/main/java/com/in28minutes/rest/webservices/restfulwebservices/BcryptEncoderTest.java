package com.in28minutes.rest.webservices.restfulwebservices;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptEncoderTest {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		for(int i=1; i<=10; i++) {
			String encodedString = encoder.encode("dummy");
			System.out.println(encodedString);
		}
		System.out.println(encoder.matches("dummy", "$2a$10$bRMFe68qmBiXrZNtzkatsuGoUUfXIxhEy2qGWAZllpHJvqkux2g6u"));
		// TODO Auto-generated method stub

	}

}
