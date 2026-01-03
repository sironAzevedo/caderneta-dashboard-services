package com.caderneta.model;

import java.time.LocalDate;

public record FaturaResponse(
        String codigo,
        String nome,
        String valor,
        LocalDate dataVencimento,
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
                this.dataVencimento,
                this.pagamentoRealizado,
                this.linkDoc,
                this.linkDocUpload,
                this.categoria,
                icon
        );
    }
}
