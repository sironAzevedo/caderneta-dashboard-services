package com.caderneta.clients.factory;

import org.springframework.stereotype.Component;

import com.caderneta.clients.UserClient;
import com.caderneta.clients.factory.fallback.UserClientFallback;

import feign.hystrix.FallbackFactory;

@Component
public class UserFallbackFactory implements FallbackFactory<UserClient> {

	@Override
	public UserClient create(Throwable cause) {
		return new UserClientFallback(cause);
	}
}
