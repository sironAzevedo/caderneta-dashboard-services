package com.caderneta.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.caderneta.clients.factory.ContasFallbackFactory;
import com.caderneta.model.ContaDTO;
import com.caderneta.model.MesDTO;

@Component
@FeignClient(name = "contas", url = "${client.contas_url}", path = "/v1/contas", fallbackFactory = ContasFallbackFactory.class)
public interface ContasClient {

	@GetMapping(value = "/mes")
	List<MesDTO> findMes();
	
	@GetMapping(value = "/by-mes")
	List<ContaDTO> findByMes(@RequestParam String email, @RequestParam Long mes);
}
