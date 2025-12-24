package com.caderneta.clients;

import com.caderneta.config.ExternalApiConfig;
import com.caderneta.model.CategoriaResponse;
import com.caderneta.model.FaturaListUserResponse;
import com.caderneta.model.HeaderInfoDTO;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
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
public class FaturaClient {
    private final WebClient webClient;
    private final ExternalApiConfig.ProductConfig.retryConfig retry;
    private final ExternalApiConfig.ProductConfig productConfig;

    public FaturaClient(@Qualifier("integrationClients") Map<String, WebClient> integrationClients,
                        ExternalApiConfig config) {
        String KEY = "fatura";
        webClient = integrationClients.get(KEY);
        this.productConfig = config.getApi().get(KEY);
        this.retry = productConfig.getRetry();
    }

    public Mono<List<CategoriaResponse>> getCategoria(String email, HeaderInfoDTO headerInfo) {
        log.info("Buscando categoria do usuario: {}", email);
        return  webClient.get()
                .uri("/{email}/categoria", email)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToFlux(CategoriaResponse.class)
                .retryWhen(getFilterRetry(retry))
                .collectList()
                .onErrorResume(e -> {
                    log.error("Erro ao buscar categoria. email={}. Error: {}", email, e.getMessage());
                    return Mono.just(List.of()); // Fallback
                });
    }

    public Mono<List<FaturaListUserResponse>> getFaturas(String email, HeaderInfoDTO headerInfo) {
        log.info("Buscando faturas do usuario: {}", email);
        return  webClient.get()
                .uri("/{email}", email)
                .headers(h -> {
                    h.add("transactionid", headerInfo.transactionId());
                    h.add("Authorization", headerInfo.token());
                })
                .retrieve()
                .bodyToFlux(FaturaListUserResponse.class)
                .retryWhen(getFilterRetry(retry))
                .collectList()
                .onErrorResume(e -> {
                    log.error("Erro ao buscar faturas. email={}. Error: {}", email, e.getMessage());
                    return Mono.just(List.of()); // Fallback
                });
    }

    private static RetryBackoffSpec getFilterRetry(ExternalApiConfig.ProductConfig.retryConfig retry) {
        return Retry.backoff(retry.getMaxAttempts(), Duration.ofMillis(retry.getBackoffMs()))
                .jitter(retry.getJitter())
                .filter(FaturaClient::isRetryableError)
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
