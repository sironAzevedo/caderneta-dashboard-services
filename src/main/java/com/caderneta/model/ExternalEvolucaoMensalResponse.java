package com.caderneta.model;

// DTO para a resposta do serviço externo e final
public record ExternalEvolucaoMensalResponse(
        String mes,
        String valorTotal,
        String previsao,
        Integer quantidade
) {}
