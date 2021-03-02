package com.caderneta.model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public enum StatusConta {
	
	A_PARGAR("A-PARGAR", "A PARGAR"),
	PAGO ("PAGO", "PAGO"),
	VENCIDO ("VENCIDO", "VENCIDO"), 
	PARCELADO ("PARCELADO", "PARCELADO");

	@Getter
	private String codigo;
	
	@Getter
	private String descricao;

	private StatusConta(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	} 
	
	public static List<StatusConta> listValues() {
        return new ArrayList<StatusConta>(Arrays.asList(StatusConta.values()));
    }

	public static StatusConta from(String cod) {
		if (cod == null) {
			return null;
		}

		for (StatusConta p : values()) {
			if (cod.equals(p.getCodigo())) {
				return p;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return descricao;
	}
}
