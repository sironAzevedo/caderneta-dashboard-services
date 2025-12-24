package com.caderneta.model;

import java.util.List;

public record DashboardResponse(
        SetupBeginResponse setupBegin,
        Boolean hasData,
        String messageNotData,
        List<GastosCategoriaResponse> gastosPorCategoria,
        List<EvolucaoMensalResponse> evolucaoMensal,
        List<FaturaResponse> faturasMes,
        List<Integer> years
) {}
