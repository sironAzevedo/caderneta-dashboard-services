package com.caderneta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.caderneta.model.DashboardDTO;
import com.caderneta.service.IDashboardService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/dashboard")
public class DashboardController {

    @Autowired
    private IDashboardService service;

    @GetMapping
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Dashboard", tags = {"Dashboard"})
    public Page<DashboardDTO> dashboard(@RequestParam(value = "email") String email, Pageable pageable) {
        return service.findAll(email, pageable);
    }
}
