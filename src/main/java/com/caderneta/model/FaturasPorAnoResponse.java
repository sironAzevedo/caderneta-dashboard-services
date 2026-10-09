package com.caderneta.model;

import java.util.List;

// DTO para a resposta do serviço externo e final
public record FaturasPorAnoResponse(
        String mes,
        String valorTotal,
        String previsao,
        Integer quantidade,
        List<FaturaResponse> faturas,
        String valorPendente
) {
    public FaturasPorAnoResponse(String mes, String valorTotal, String previsao, Integer quantidade, List<FaturaResponse> faturas) {
        this(mes, valorTotal, previsao, quantidade, faturas, null);
    }

    public FaturasPorAnoResponse withFaturas(List<FaturaResponse> faturas) {
        return new FaturasPorAnoResponse(
                this.mes,
                this.valorTotal,
                this.previsao,
                this.quantidade,
                faturas,
                this.valorPendente);
    }

    public FaturasPorAnoResponse withValorPendente(String valorPendente) {
        return new FaturasPorAnoResponse(
                this.mes,
                this.valorTotal,
                this.previsao,
                this.quantidade,
                this.faturas,
                valorPendente);
    }

    public FaturasPorAnoResponse withFaturasEValorPendente(List<FaturaResponse> faturas, String valorPendente) {
        return new FaturasPorAnoResponse(
                this.mes,
                this.valorTotal,
                this.previsao,
                this.quantidade,
                faturas,
                valorPendente);
    }
}
