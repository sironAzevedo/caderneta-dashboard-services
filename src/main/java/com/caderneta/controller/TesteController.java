package com.caderneta.controller;

import com.br.azevedo.infra.log.method.MethodLoggable;
import com.br.azevedo.security.user.ValidationUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/teste")
public class TesteController {

    @ValidationUser
    @MethodLoggable
    @GetMapping
    @Operation(
            summary = "Resumo do Dashboard",
            description = "Retorna o resumo financeiro do usuário para o ano e mês informados.",
            tags = {"Dashboard"}
    )
    public ResponseEntity<String> string() {
        return ResponseEntity.ok("response");
    }
}
