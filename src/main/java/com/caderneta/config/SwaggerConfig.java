package com.caderneta.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
	
	private static final String ERROR_DETAIL = "StandardError";
	
	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
					.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
					.paths(PathSelectors.any())
					.build()
					.apiInfo(metaData())
					.useDefaultResponseMessages(false)
					.globalResponseMessage(RequestMethod.GET, messageGET())
					.globalResponseMessage(RequestMethod.POST, messagePOST())
					.globalResponseMessage(RequestMethod.DELETE, messageDELETE());
		
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");
		
		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars");
	}
	
	
	private ApiInfo metaData() {
		return new ApiInfoBuilder()
				.title("API Dashboard")
				.license("76aoj-Grupo 1")
				.build();
	}
	
	private List<ResponseMessage> messageGET() {
		return List.of(
				new ResponseMessageBuilder().code(HttpStatus.OK.value()).message(HttpStatus.OK.getReasonPhrase()).build(),
				new ResponseMessageBuilder().code(HttpStatus.NOT_FOUND.value()).message(HttpStatus.NOT_FOUND.getReasonPhrase()).responseModel(new ModelRef(ERROR_DETAIL)).build());
	}
	
	private List<ResponseMessage> messagePOST() {
		return List.of(
				new ResponseMessageBuilder().code(HttpStatus.CREATED.value()).message(HttpStatus.CREATED.getReasonPhrase()).build(),
				new ResponseMessageBuilder().code(HttpStatus.NOT_FOUND.value()).message(HttpStatus.NOT_FOUND.getReasonPhrase()).responseModel(new ModelRef(ERROR_DETAIL)).build());
	}

	private List<ResponseMessage> messageDELETE() {
		return List.of(
				new ResponseMessageBuilder().code(HttpStatus.NO_CONTENT.value()).message(HttpStatus.NO_CONTENT.getReasonPhrase()).build(),
				new ResponseMessageBuilder().code(HttpStatus.NOT_FOUND.value()).message(HttpStatus.NOT_FOUND.getReasonPhrase()).responseModel(new ModelRef(ERROR_DETAIL)).build());
	}
}
