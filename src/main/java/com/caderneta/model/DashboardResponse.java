package com.caderneta.model;

import java.util.List;

public record DashboardResponse(
        List<StatsResponse> stats,
        List<GastosCategoriaResponse> gastosPorCategoria,
        List<EvolucaoMensalResponse> evolucaoMensal,
        List<FaturasDashboardResponse> faturasMes,
        List<Integer> years
) {}
