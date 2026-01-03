package com.caderneta.repository;

import com.caderneta.model.*;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IFaturaRepository {
    Mono<List<CategoriaResponse>> getCategoria(String email, HeaderInfoDTO headerInfo);

    Mono<List<FaturaListUserResponse>> getFatura(String email, HeaderInfoDTO headerInfo);
}
