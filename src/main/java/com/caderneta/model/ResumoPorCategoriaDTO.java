package com.caderneta.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumoPorCategoriaDTO {
    private String categoria;
    private BigDecimal valorTotal;
    private BigDecimal percentualDoSalario;
}
