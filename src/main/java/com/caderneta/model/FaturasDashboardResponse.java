package com.caderneta.model;

import java.math.BigDecimal;

public record FaturasDashboardResponse(
        String name,
        BigDecimal amount,
        String dueDate,
        String priority
) {}
