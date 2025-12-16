package com.caderneta.model;

public record StatsResponse(
        String title,
        String value,
        String change,
        String icon,
        String trend
) {}
