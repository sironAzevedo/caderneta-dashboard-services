package com.caderneta.model;

import java.util.List;

// DTO para a resposta do serviço externo e final
public record FaturasPorAnoResponse(
        String mes,
        String valorTotal,
        String previsao,
        Integer quantidade,
        List<FaturaResponse> faturas
) {
    public FaturasPorAnoResponse withFaturas(List<FaturaResponse> faturas) {
        return new FaturasPorAnoResponse(
                this.mes,
                this.valorTotal,
                this.previsao,
                this.quantidade,
                faturas);
    }
}
