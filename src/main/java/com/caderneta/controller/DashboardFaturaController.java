package com.caderneta.controller;

import com.br.azevedo.infra.log.method.MethodLoggable;
import com.br.azevedo.security.user.ValidationUser;
import com.caderneta.model.DashboardFaturaResponse;
import com.caderneta.model.HeaderInfoDTO;
import com.caderneta.service.IDashboardFaturaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/dashboard/fatura")
public class DashboardFaturaController {

    private final IDashboardFaturaService service;

    @ValidationUser
    @MethodLoggable
    @GetMapping("/{email}/summary")
    @Operation(
            summary = "Dashboard de faturas",
            description = "Retorna as informações das faturas de todo ano ou de um mes especifico",
            tags = {"Fatura Dashboard"}
    )
    public ResponseEntity<DashboardFaturaResponse> DashboardFatura(
            @PathVariable String email,
            @RequestParam Integer ano,
            @RequestParam(required = false, defaultValue = "0") Integer mes,
            @RequestHeader(name = "transactionid") String transactionId,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token
    ) {
        HeaderInfoDTO headerInfo = new HeaderInfoDTO(transactionId, token);
        DashboardFaturaResponse response = service.getDashboardSummary(email, mes, ano, headerInfo);
        return ResponseEntity.ok(response);
    }
}
