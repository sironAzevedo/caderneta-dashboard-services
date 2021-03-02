package com.caderneta.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.caderneta.clients.ContasClient;
import com.caderneta.clients.UserClient;
import com.caderneta.handler.exception.UserException;
import com.caderneta.model.ContaDTO;
import com.caderneta.model.DashboardDTO;
import com.caderneta.model.MesDTO;
import com.caderneta.model.UserDTO;
import com.caderneta.service.IDashboardService;
import com.caderneta.util.Utils;

@Service
public class DashboardServiceImpl implements IDashboardService {

	@Autowired
	private UserClient userClient;

	@Autowired
	private ContasClient contasClient;

	@Override
	public Page<DashboardDTO> findAll(String email, Pageable pageable) {
		List<DashboardDTO> result = new ArrayList<>();
		UserDTO user = this.getUser(email);
		List<MesDTO> meses = contasClient.findMes();

		for (MesDTO mes : meses) {
			List<ContaDTO> contas = contasClient.findByMes(user.getEmail(), mes.getCodigo());
			
			if(!contas.isEmpty()) {
				double totalGastos = contas.stream()
						.mapToDouble(x -> Utils.formatValor(x.getValorConta()).doubleValue())
						.reduce(0, Double::sum);
				
				DashboardDTO dto = DashboardDTO
						.builder()
						.codigo(mes.getCodigo())
						.mes(mes.getDsMes())
						.qtdConta(contas.size())
						.totalGastos(Utils.formatValor(BigDecimal.valueOf(totalGastos)))
						.build();
				
				result.add(dto);
			}
		}

		return new PageImpl<>(result, pageable, result.size());
	}
	
	private UserDTO getUser(String user) {
		return Optional.ofNullable(userClient.findByEmail(user)).orElseThrow(() -> new UserException("User not found"));
	}
}