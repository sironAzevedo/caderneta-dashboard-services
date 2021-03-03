package com.caderneta.clients.factory;

import org.springframework.stereotype.Component;

import com.caderneta.clients.ContasClient;
import com.caderneta.clients.factory.fallback.ContasClientFallback;

import feign.hystrix.FallbackFactory;

@Component
public class ContasFallbackFactory implements FallbackFactory<ContasClient> {

	@Override
	public ContasClient create(Throwable cause) {
		return new ContasClientFallback(cause);
	}
}
