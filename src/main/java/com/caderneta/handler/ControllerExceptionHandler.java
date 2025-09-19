package com.caderneta.handler;

import java.util.Date;


import com.br.azevedo.exception.EmptyResultDataAccessException;
import com.br.azevedo.exception.InternalErrorException;
import com.br.azevedo.exception.NotFoundException;
import com.br.azevedo.exception.UserException;
import com.br.azevedo.model.vo.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

	@ResponseBody
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = { EmptyResultDataAccessException.class, NotFoundException.class, UserException.class })
	public StandardError tNotFound(RuntimeException e, HttpServletRequest request) {
		return StandardError.builder(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date());
	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = { InternalErrorException.class })
	public StandardError internalError(RuntimeException e, HttpServletRequest request) {
		return StandardError.builder(HttpStatus.NOT_FOUND.value(), e.getMessage(), new Date());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		StandardError error = StandardError.builder(HttpStatus.BAD_REQUEST.value(), "Error de validação", new Date(),
				ex.getBindingResult().getFieldErrors());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
															 HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		return new ResponseEntity<>(StandardError.builder(statusCode.value(), ex.getLocalizedMessage(), new Date()),
				headers, statusCode);
	}
}
