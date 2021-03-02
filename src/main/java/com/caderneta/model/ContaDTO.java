package com.caderneta.model;

import java.io.Serializable;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContaDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long codigo;
	
	@NotEmpty(message = "Campo Obrigatorio")
	private String emailUser;

	@NotEmpty(message = "Campo Obrigatorio")
	private String valorConta;

	@NotNull(message = "Campo Obrigatorio")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "America/Sao_Paulo")
	private Date dataVencimento;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "America/Sao_Paulo")
	private Date dataPagamento;
	
	@Valid
	@NotNull(message = "Campo Obrigatorio")
	private MesDTO mes;
	
	@NotNull(message = "Campo Obrigatorio")
	private StatusContaDTO status;
	
	private Integer qtdParcelas;
	private String comentario;

	@Valid
	@NotNull(message = "Campo Obrigatorio")
	private TipoContaDTO tipoConta;
}
