package com.caderneta.model;

import java.math.BigDecimal;

public record GastosCategoriaResponse(
        String name,
        BigDecimal salary,
        BigDecimal value,
        String color
) {}
