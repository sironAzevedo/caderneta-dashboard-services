package com.caderneta.model;

import java.util.List;

public record DashboardResponse(
        List<GastosCategoriaResponse> gastosPorCategoria,
        List<EvolucaoMensalResponse> evolucaoMensal,
        List<FaturaResponse> faturasMes,
        List<Integer> years
) {}
