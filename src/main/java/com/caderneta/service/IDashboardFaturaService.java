package com.caderneta.service;

import com.caderneta.model.DashboardFaturaResponse;
import com.caderneta.model.DashboardResponse;
import com.caderneta.model.FaturaCategoriaDetalheDTO;
import com.caderneta.model.HeaderInfoDTO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IDashboardFaturaService {
    DashboardFaturaResponse getDashboardSummary(String email, Integer mes, Integer ano, HeaderInfoDTO headerInfo);

    Flux<FaturaCategoriaDetalheDTO> getReports(String email, String categoria, Integer ano, HeaderInfoDTO headerInfo);
}
