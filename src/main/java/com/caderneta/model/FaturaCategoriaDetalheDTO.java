package com.caderneta.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaturaCategoriaDetalheDTO {
    private String categoria;
    private String icon;
    private String color;
    private BigDecimal valorTotal;
    private BigDecimal percentualSalario;
    private List<FaturaDetalheDTO> faturas;
}
