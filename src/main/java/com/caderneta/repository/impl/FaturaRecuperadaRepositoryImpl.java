package com.caderneta.repository.impl;

import com.caderneta.clients.FaturaRecuperadaClient;
import com.caderneta.model.*;
import com.caderneta.repository.IFaturaRecuperadaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FaturaRecuperadaRepositoryImpl implements IFaturaRecuperadaRepository {
    private final FaturaRecuperadaClient faturaRecuperadaClient;

    @Override
    public Mono<List<ExternalGastosCategoriaResponse>> getGastosPorCategoria(String email, int ano, HeaderInfoDTO headerInfo) {
        return faturaRecuperadaClient.getGastosPorCategoria(email, ano, headerInfo);
    }

    @Override
    public Mono<List<ExternalEvolucaoMensalResponse>> getEvolucaoMensal(String email, int ano, HeaderInfoDTO headerInfo) {
        return faturaRecuperadaClient.getEvolucaoMensal(email, ano, headerInfo);
    }

    @Override
    public Mono<ProximasFaturasResponse> getFaturasPorMes(String email, int mes, int ano, HeaderInfoDTO headerInfo, String pagamentoRealizado) {
        return faturaRecuperadaClient.getFaturasPorMes(email, mes, ano, headerInfo, pagamentoRealizado);
    }

    @Override
    public Mono<List<FaturasPorAnoResponse>> getFaturasPorAno(String email, Integer ano, HeaderInfoDTO headerInfo) {
        return faturaRecuperadaClient.getFaturasPorAno(email, ano, headerInfo);
    }

    @Override
    public Mono<List<Integer>> getAnosFaturasRecuperadas(String email, HeaderInfoDTO headerInfo) {
        return faturaRecuperadaClient.getAnosFaturasRecuperadas(email, headerInfo);
    }
}
