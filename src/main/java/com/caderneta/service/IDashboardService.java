package com.caderneta.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.caderneta.model.DashboardDTO;

public interface IDashboardService {

	Page<DashboardDTO> findAll(String email, Pageable pageable);
}
