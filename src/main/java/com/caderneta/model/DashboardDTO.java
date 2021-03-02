package com.caderneta.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Long codigo;

	@Getter
	@Setter
	private String mes;
	
//	@Getter
//	@Setter
//	private Integer ano;
//
//	@Getter
//	@Setter
//	private String salario;

	@Getter
	@Setter
	private Integer qtdConta;

	@Getter
	@Setter
	private String totalGastos;

	@Getter
	@Setter
	private String saldoFinal; 
}
