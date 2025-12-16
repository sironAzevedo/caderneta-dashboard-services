package com.caderneta.model;

import java.util.List;

public record DashboardFaturaResponse(
        List<Integer> years,
        List<MesDTO> meses,
        List<StatsResponse> status,
        List<FaturasPorAnoResponse> faturas
) {}
