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
public class FaturaDetalheDTO {
    private String fatura;
    private BigDecimal valor;
    private List<FaturaMesDTO> itens;
}
