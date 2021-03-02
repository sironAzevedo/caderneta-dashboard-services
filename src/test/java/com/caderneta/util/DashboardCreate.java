package com.caderneta.util;

import java.util.Date;

import com.caderneta.model.ContaDTO;
import com.caderneta.model.MesDTO;
import com.caderneta.model.StatusContaDTO;
import com.caderneta.model.TipoContaDTO;

public final class DashboardCreate {

	public static ContaDTO contaDTO() {
		return ContaDTO
				.builder()
				.codigo(1L)
				.emailUser("teste.silva@email.com")
				.valorConta("1000")
				.dataVencimento(new Date())
				.dataPagamento(new Date())
				.qtdParcelas(1)
				.comentario("teste")
				.mes(mes())
				.status(status())
				.tipoConta(tipo())
				.build();
	}
	
	public static MesDTO mes() {
		return MesDTO
				.builder()
				.codigo(1L)
				.dsMes("JANEIRO")
				.build();
	}
	
	public static StatusContaDTO status() {
		return StatusContaDTO
				.builder()
				.codigo(2L)
				.descricao("PAGO")
				.build();
	}
	
	public static TipoContaDTO tipo() {
		return TipoContaDTO
				.builder()
				.codigo(1L)
				.tipo("ALUGUEL")
				.descricao("ALUGUEL")
				.build();
	}
	
}
