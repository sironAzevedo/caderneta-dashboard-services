package com.caderneta.service;

import com.caderneta.model.DashboardResponse;
import com.caderneta.model.HeaderInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.caderneta.model.DashboardDTO;

public interface IDashboardService {
    DashboardResponse getDashboardSummary(String email, int mes, int ano, HeaderInfoDTO headerInfo);
}
