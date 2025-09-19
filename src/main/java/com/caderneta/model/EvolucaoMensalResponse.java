package com.caderneta.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

// DTO para a resposta do serviço externo e final
public record EvolucaoMensalResponse(
        String month,
        BigDecimal value
) {}
