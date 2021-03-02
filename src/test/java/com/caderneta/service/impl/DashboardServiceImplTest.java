package com.caderneta.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.caderneta.clients.ContasClient;
import com.caderneta.clients.UserClient;
import com.caderneta.handler.exception.UserException;
import com.caderneta.model.ContaDTO;
import com.caderneta.model.DashboardDTO;
import com.caderneta.model.UserDTO;
import com.caderneta.service.IDashboardService;
import com.caderneta.util.DashboardCreate;

@SpringBootTest
class DashboardServiceImplTest {
	
	@Autowired(required=true)
	private IDashboardService service;
	
	@MockBean
	private UserClient userClient;

	@MockBean
	private ContasClient contasClient;
	
	private UserDTO user;
	private ContaDTO account;
	
	@BeforeEach
	void setup() {
		user = new UserDTO();
		user.setId(1L);
		user.setName("Test da Silva");
		user.setEmail("test.silva@email.com");
		
		account = DashboardCreate.contaDTO();		
	}

	@Test
	void when_findAll_return_sucess() {
		when(userClient.findByEmail(ArgumentMatchers.anyString())).thenReturn(user);
		when(contasClient.findMes()).thenReturn(List.of(DashboardCreate.mes()));
		when(contasClient.findByMes(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(List.of(account));
		
		PageRequest pageable = PageRequest.of(0, 10);
		Page<DashboardDTO> res = service.findAll("test.silva@email.com", pageable);
		assertNotNull(res.getContent());
	}
	
	@Test
	void when_findAll_and_userNotExist_returnUserException() {
		when(userClient.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
		
		PageRequest pageable = PageRequest.of(0, 10);
		
		Assertions
		.assertThatExceptionOfType(UserException.class)
		.isThrownBy(() -> service.findAll("test.silva@email.com", pageable));
		
	}

}
