package com.caderneta.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TipoContaDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Long codigo;

	@Getter
	@Setter
	private String tipo;

	@Getter
	@Setter
	private String descricao;
}
