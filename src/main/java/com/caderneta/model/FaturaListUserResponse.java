package com.caderneta.model;

public record FaturaListUserResponse(
        String codigo,
        String nome,
        String tipoFatura,
        String tipoResgate,
        String status,
        String typeDocumento
) {

}
