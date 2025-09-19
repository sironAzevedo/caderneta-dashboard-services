package com.caderneta.clients;

import com.caderneta.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class FaturaRecuperadaClient {
    private final WebClient webClient;

    public FaturaRecuperadaClient(@Qualifier("integrationClients") Map<String, WebClient> integrationClients) {
        webClient = integrationClients.get("fatura-recuperada");
    }

    public Mono<List<ExternalGastosCategoriaResponse>> getGastosPorCategoria(String email, int ano, HeaderInfoDTO headerInfo) {
        log.info("Buscando gastos por categoria para usuario: {}, ano: {}", email, ano);
        return webClient.get()
                .uri("/{email}/metrics/{ano}/balance-invoice", email, ano)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToFlux(ExternalGastosCategoriaResponse.class)
                .collectList()
                .onErrorResume(e -> {
                    log.error("Erro ao buscar gastos por categoria. email={}, ano={}. Error: {}", email, ano, e.getMessage());
                    return Mono.just(List.of()); // Fallback
                });
    }

    public Mono<List<ExternalEvolucaoMensalResponse>> getEvolucaoMensal(String email, int ano, HeaderInfoDTO headerInfo) {
        log.info("Buscando evolução mensal para usuario: {}, ano: {}", email, ano);
        return webClient.get()
                .uri("/{email}/ano?ano={ano}", email, ano)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToFlux(ExternalEvolucaoMensalResponse.class)
                .collectList()
                .onErrorResume(e -> {
                    log.error("Erro ao buscar evolução mensal. usuario={}, ano={}. Error: {}", email, ano, e.getMessage());
                    return Mono.just(List.of()); // Fallback
                });
    }

    public Mono<ProximasFaturasResponse> getFaturasPorMes(String email, int mes, int ano, HeaderInfoDTO headerInfo) {
        log.info("Buscando faturas para usuario: {}, mes: {}, ano: {}", email, mes, ano);
        return webClient.get()
                .uri("/{email}/mes&ano?mes={mes}&ano={ano}", email, mes, ano)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToMono(ProximasFaturasResponse.class)
                .onErrorResume(e -> {
                    log.error("Erro ao buscar faturas por mês. usuario={}, mes={}, ano={}. Error: {}", email, mes, ano, e.getMessage());
                    return Mono.empty(); // Fallback
                });
    }

    public Mono<List<Integer>> getAnosFaturasRecuperadas(String email, HeaderInfoDTO headerInfo) {
        log.info("Buscando lista de anos de faturas recuperadas para usuario: {}", email);
        return webClient.get()
                .uri("/{email}/years", email)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToFlux(Integer.class)
                .collectList()
                .onErrorResume(e -> {
                    log.error("Erro ao buscar lista de anos de faturas recuperadas. usuario={}. Error: {}", email, e.getMessage());
                    return Mono.empty(); // Fallback
                });
    }
}
