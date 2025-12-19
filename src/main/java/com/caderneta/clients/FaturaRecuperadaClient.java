package com.caderneta.clients;

import com.caderneta.config.ExternalApiConfig;
import com.caderneta.model.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FaturaRecuperadaClient {
    private final WebClient webClient;
    private final String KEY = "fatura-recuperada";
    private final ExternalApiConfig.ProductConfig.retryConfig retry;
    private final ExternalApiConfig.ProductConfig productConfig;

    public FaturaRecuperadaClient(@Qualifier("integrationClients") Map<String, WebClient> integrationClients,
                                  ExternalApiConfig config) {
        webClient = integrationClients.get(KEY);
        this.productConfig = config.getApi().get(KEY);
        this.retry = productConfig.getRetry();
    }

    public Mono<List<ExternalGastosCategoriaResponse>> getGastosPorCategoria(String email, int ano, HeaderInfoDTO headerInfo) {
        log.info("Buscando gastos por categoria para usuario: {}, ano: {}", email, ano);
        return  webClient.get()
                .uri("/{email}/metrics/{ano}/balance-invoice", email, ano)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToFlux(ExternalGastosCategoriaResponse.class)
                .retryWhen(getFilterRetry(retry))
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
                .retryWhen(getFilterRetry(retry))
                .collectList()
                .onErrorResume(e -> {
                    log.error("Erro ao buscar evolução mensal. usuario={}, ano={}. Error: {}", email, ano, e.getMessage());
                    return Mono.just(List.of()); // Fallback
                });
    }

    public Mono<ProximasFaturasResponse> getFaturasPorMes(String email, int mes, int ano, HeaderInfoDTO headerInfo, String pagamentoRealizado) {
        log.info("Buscando faturas para usuario: {}, mes: {}, ano: {}", email, mes, ano);

        var uri = "/{email}/mes&ano?mes={mes}&ano={ano}";
        Object[] params = {email, mes, ano};

        if (StringUtils.isNotBlank(pagamentoRealizado)) {
            uri = uri.concat("&pagamentoRealizado={pagamentoRealizado}");
            params = ArrayUtils.add(params, pagamentoRealizado);
        }

        return webClient.get()
                .uri(uri, params)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToMono(ProximasFaturasResponse.class)
                .retryWhen(getFilterRetry(retry))
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

    public Mono<List<FaturasPorAnoResponse>> getFaturasPorAno(String email, Integer ano, HeaderInfoDTO headerInfo) {
        log.info("Buscando faturas para usuario: {}, ano: {}", email, ano);
        return webClient.get()
                .uri("/{email}/ano?ano={ano}", email, ano)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToFlux(FaturasPorAnoResponse.class)
                .retryWhen(getFilterRetry(retry))
                .collectList()
                .onErrorResume(e -> {
                    log.error("Erro ao buscar faturas por ano. usuario={}, ano={}. Error: {}", email, ano, e.getMessage());
                    return Mono.just(List.of()); // Fallback
                });
    }

    private static RetryBackoffSpec getFilterRetry(ExternalApiConfig.ProductConfig.retryConfig retry) {
        return Retry.backoff(retry.getMaxAttempts(), Duration.ofMillis(retry.getBackoffMs()))
                .jitter(retry.getJitter())
                .filter(FaturaRecuperadaClient::isRetryableError)
                .doBeforeRetry(retrySignal ->
                        log.warn(
                                "Retry [{}] - tentativa {}",
                                retrySignal.failure().getClass().getSimpleName(),
                                retrySignal.totalRetries() + 1
                        )
                )
                .onRetryExhaustedThrow((spec, signal) -> {
                    log.error(
                            "Retry esgotado para integração após {} tentativas",
                            retry.getMaxAttempts()
                    );
                    return signal.failure();
                });
    }

    private static boolean isRetryableError(Throwable throwable) {

        // Unwrap common reactor/netty wrappers to inspect the real cause
        Throwable unwrapped = Exceptions.unwrap(throwable);

        // 1) Timeout / ReadTimeout from Netty or other timeout instances
        if ((unwrapped instanceof ReadTimeoutException || unwrapped instanceof TimeoutException)) {
            return true;
        }

        // 2) Reactor Netty may surface a PrematureCloseException (connection closed before response)
        //    treat it as retryable for GETs as well
        if (unwrapped != null && "reactor.netty.http.client.PrematureCloseException".equals(unwrapped.getClass().getName())) {
            return true;
        }

        // 3) Walk the cause chain to find nested ReadTimeout/Timeout (defensive)
        Throwable cause = unwrapped;
        while (cause != null) {
            if ((cause instanceof ReadTimeoutException || cause instanceof TimeoutException)) {
                return true;
            }
            cause = cause.getCause();
        }

        // 4) HTTP 5xx responses should be retried
        if (unwrapped instanceof org.springframework.web.reactive.function.client.WebClientResponseException ex) {
            return ex.getStatusCode().is5xxServerError();
        }

        return false;
    }
}
