package com.caderneta.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class XssProtectionConfig implements WebMvcConfigurer {

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(converters.size() - 1, jsonConverter());
	}

	@Bean
	public HttpMessageConverter<?> jsonConverter() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(String.class, new DefaultJsonDeerializer());
		ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
		mapper.registerModule(module);
		return new MappingJackson2HttpMessageConverter(mapper);
	} 
}
