package com.caderneta.clients.factory.fallback;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caderneta.clients.ContasClient;
import com.caderneta.model.ContaDTO;
import com.caderneta.model.MesDTO;

public class ContasClientFallback implements ContasClient {
	private static final Logger log = LoggerFactory.getLogger(ContasClientFallback.class);
	
	private final Throwable cause;
	
	public ContasClientFallback(Throwable cause) {
		this.cause = cause;
	}

	@Override
	public List<MesDTO> findMes() {
		log.info("Error API Category: " + cause.getLocalizedMessage());
		return List.of();
	}

	@Override
	public List<ContaDTO> findByMes(String email, Long mes) {
		log.info("Error API Category: " + cause.getLocalizedMessage());
		return List.of();
	}

	
}
