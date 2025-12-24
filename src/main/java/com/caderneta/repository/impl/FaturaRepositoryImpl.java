package com.caderneta.repository.impl;

import com.caderneta.clients.FaturaClient;
import com.caderneta.model.CategoriaResponse;
import com.caderneta.model.FaturaListUserResponse;
import com.caderneta.model.HeaderInfoDTO;
import com.caderneta.repository.IFaturaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FaturaRepositoryImpl implements IFaturaRepository {
    private final FaturaClient client;

    @Override
    public Mono<List<CategoriaResponse>> getGastosPorCategoria(String email, HeaderInfoDTO headerInfo) {
        return client.getCategoria(email, headerInfo);
    }

    @Override
    public Mono<List<FaturaListUserResponse>> getFatura(String email, HeaderInfoDTO headerInfo) {
        return client.getFaturas(email, headerInfo);
    }
}
