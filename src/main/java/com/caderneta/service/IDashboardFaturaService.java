package com.caderneta.service;

import com.caderneta.model.DashboardFaturaResponse;
import com.caderneta.model.HeaderInfoDTO;

public interface IDashboardFaturaService {
    DashboardFaturaResponse getDashboardSummary(String email, Integer mes, Integer ano, HeaderInfoDTO headerInfo);
}
