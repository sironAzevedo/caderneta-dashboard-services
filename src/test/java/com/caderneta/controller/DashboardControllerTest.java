package com.caderneta.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.caderneta.service.IDashboardService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DashboardController.class)
class DashboardControllerTest {
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IDashboardService service;

	@Test
	void whenDashboard_thenReturns200() throws Exception {
		mockMvc.perform(get("/v1/dashboard")
		        .contentType(MediaType.APPLICATION_JSON)
		        .param("email", "email@test.com")
		        .param("page", "0")
		        .param("size", "10")
		        
				)
		        .andExpect(status().isOk());	
	}

}
