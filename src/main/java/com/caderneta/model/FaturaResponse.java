package com.caderneta.model;

import java.time.LocalDate;

public record FaturaResponse(
        String codigo,
        String nome,
        String valor,
        LocalDate dataRecuperacao,
        String pagamentoRealizado,
        String linkDoc,
        String linkDocUpload,
        String categoria,
        String icon
) {
    public FaturaResponse withIcon(String icon) {
        return new FaturaResponse(
                this.codigo,
                this.nome,
                this.valor,
                this.dataRecuperacao,
                this.pagamentoRealizado,
                this.linkDoc,
                this.linkDocUpload,
                this.categoria,
                icon
        );
    }
}
