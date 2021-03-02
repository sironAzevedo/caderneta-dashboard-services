package com.caderneta.clients.factory.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caderneta.clients.UserClient;
import com.caderneta.model.UserDTO;

public class ContasClientFallback implements UserClient {
	private static final Logger log = LoggerFactory.getLogger(ContasClientFallback.class);
	
	private final Throwable cause;
	
	public ContasClientFallback(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public UserDTO findByEmail(String email) {
		log.info("Error API Category: " + cause.getLocalizedMessage());
		return null;
	} 
}
