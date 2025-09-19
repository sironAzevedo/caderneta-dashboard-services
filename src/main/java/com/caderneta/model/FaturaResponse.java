package com.caderneta.model;

import java.time.LocalDate;

public record FaturaResponse(
        String codigo,
        String nome,
        String valor,
        LocalDate dataRecuperacao,
        String pagamentoRealizado,
        String linkDoc,
        String linkDocUpload
) {}
