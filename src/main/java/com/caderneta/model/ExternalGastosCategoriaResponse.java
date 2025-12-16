package com.caderneta.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO para a resposta do serviço externo
public record ExternalGastosCategoriaResponse(
        String nome,
        @JsonProperty("valorTotal") String valorTotal
) {}
