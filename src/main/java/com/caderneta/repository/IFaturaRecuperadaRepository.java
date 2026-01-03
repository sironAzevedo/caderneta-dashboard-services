package com.caderneta.repository;

import com.caderneta.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IFaturaRecuperadaRepository {
    Mono<List<ExternalGastosCategoriaResponse>> getGastosPorCategoria(String email, int ano, HeaderInfoDTO headerInfo);

    Mono<List<ExternalEvolucaoMensalResponse>> getEvolucaoMensal(String email, int ano, HeaderInfoDTO headerInfo);

    Mono<ProximasFaturasResponse> getFaturasPorMes(String email, int mes, int ano, HeaderInfoDTO headerInfo, String pagamentoRealizado);

    Mono<List<FaturasPorAnoResponse>> getFaturasPorAno(String email, Integer ano, HeaderInfoDTO headerInfo);

    Mono<List<Integer>> getAnosFaturasRecuperadas(String email, HeaderInfoDTO headerInfo);

    Mono<List<ResumoPorCategoriaDTO>> getResumoPorCategoria(String email, int ano, HeaderInfoDTO headerInfo);

    Flux<FaturaCategoriaDetalheDTO> getReport(String email, String categoria, int ano, HeaderInfoDTO headerInfo);
}
