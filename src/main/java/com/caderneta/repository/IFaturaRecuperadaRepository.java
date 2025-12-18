package com.caderneta.repository;

import com.caderneta.model.*;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IFaturaRecuperadaRepository {
    Mono<List<ExternalGastosCategoriaResponse>> getGastosPorCategoria(String email, int ano, HeaderInfoDTO headerInfo);

    Mono<List<ExternalEvolucaoMensalResponse>> getEvolucaoMensal(String email, int ano, HeaderInfoDTO headerInfo);

    Mono<ProximasFaturasResponse> getFaturasPorMes(String email, int mes, int ano, HeaderInfoDTO headerInfo, String pagamentoRealizado);

    Mono<List<FaturasPorAnoResponse>> getFaturasPorAno(String email, Integer ano, HeaderInfoDTO headerInfo);

    Mono<List<Integer>> getAnosFaturasRecuperadas(String email, HeaderInfoDTO headerInfo);
}
