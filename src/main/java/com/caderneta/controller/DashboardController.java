package com.caderneta.controller;

import com.br.azevedo.infra.log.method.MethodLoggable;
import com.br.azevedo.security.user.ValidationUser;
import com.caderneta.model.DashboardResponse;
import com.caderneta.model.HeaderInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.caderneta.model.DashboardDTO;
import com.caderneta.service.IDashboardService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/dashboard")
public class DashboardController {

    private final IDashboardService dashboardService;

    @ValidationUser
    @MethodLoggable
    @GetMapping("/{email}/summary")
    @Operation(
            summary = "Resumo do Dashboard",
            description = "Retorna o resumo financeiro do usuário para o ano e mês informados.",
            tags = {"Dashboard"}
    )
    public ResponseEntity<DashboardResponse> getDashboardSummary(
            @PathVariable String email,
            @RequestParam int ano,
            @RequestParam int mes,
            @RequestHeader(name = "transactionid") String transactionId,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token
    ) {
        HeaderInfoDTO headerInfo = new HeaderInfoDTO(transactionId, token);
        DashboardResponse response = dashboardService.getDashboardSummary(email, mes, ano, headerInfo);
        return ResponseEntity.ok(response);
    }
}
