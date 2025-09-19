package com.caderneta.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// DTO para a resposta do serviço externo e final
public record ProximasFaturasResponse(
        String valorTotal,
        String previsao,
        List<FaturaResponse> faturas
) {}
