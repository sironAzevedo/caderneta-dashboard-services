package com.caderneta.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final ExternalApiConfig externalApiConfig;
    private final ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory;

    private static final String TRANSACTION_ID_HEADER = "transactionid";

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse());
    }

    @Bean
    @Qualifier("integrationClients")
    public Map<String, WebClient> integrationClients(WebClient.Builder webClientBuilder) {
        return externalApiConfig.getApi().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {

                            var apiConfig = entry.getValue();
                            return webClientBuilder
                                    .clone()
                                    .clientConnector(
                                            new ReactorClientHttpConnector(
                                                    createHttpClient(
                                                            apiConfig.getRequestTimeout(),
                                                            apiConfig.getConnectionTimeout()
                                                    )
                                            )
                                    )
                                    .baseUrl(apiConfig.getBaseUrl())
                                    .filter(retryFilter(
                                            apiConfig.getRetry()
                                    ))
                                    .filter(resilienceFilter(entry.getKey()))
                                    .build();
                        }
                ));
    }

    private static HttpClient createHttpClient(Integer requestTimeout, Integer connectionTimeout) {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(requestTimeout, TimeUnit.MILLISECONDS));
                });
        return HttpClient.from(tcpClient);
    }

    // Filtros para logging (opcional)
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Realizando chamada para o produto: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            return clientResponse.bodyToMono(String.class)
                    .defaultIfEmpty("") // caso o body seja vazio
                    .flatMap(body -> {
                        log.info("O produto [{}] retornou status: [{}]", clientResponse.request().getURI(), clientResponse.statusCode());

                        if (!clientResponse.statusCode().is2xxSuccessful()) {
                            log.info("Body da resposta: {}", body);
                        }

                        // Recria o clientResponse com o body lido, para não consumir o body no pipeline
                        ClientResponse newResponse = ClientResponse.create(clientResponse.statusCode())
                                .headers(headers -> headers.addAll(clientResponse.headers().asHttpHeaders()))
                                .body(body)
                                .build();

                        return Mono.just(newResponse);
                    });
        });
    }

    private ExchangeFilterFunction retryFilter(ExternalApiConfig.ProductConfig.retryConfig config) {
        return (request, next) ->
                next.exchange(request)
                        .retryWhen(
                                Retry.backoff(config.getMaxAttempts() - 1, Duration.ofMillis(config.getBackoffMs()))
                                        .jitter(config.getJitter())
                                        .filter(r -> isRetryableError(r, request))
                                        .onRetryExhaustedThrow((spec, signal) -> {
                                            log.error(
                                                    "Retry esgotado para integração [{}] após {} tentativas",
                                                    request.url().getPath(),
                                                    config.getMaxAttempts()
                                            );
                                            return signal.failure();
                                        })
                                        .doBeforeRetry(retrySignal ->
                                                log.warn(
                                                        "Retry [{}] para integração [{}] - tentativa {}",
                                                        retrySignal.failure().getClass().getSimpleName(),
                                                        request.url().getPath(),
                                                        retrySignal.totalRetries() + 1
                                                )
                                        )
                        );
    }

    private boolean isRetryableError(Throwable throwable, ClientRequest request) {

        // Timeout de leitura
        if (throwable instanceof TimeoutException &&  request.method() == HttpMethod.GET) {
            return true;
        }

        // Erros de conexão
        if (Exceptions.isRetryExhausted(throwable)) {
            return false;
        }

        // Erros HTTP 5xx
        if (throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException ex) {
            return ex.getStatusCode().is5xxServerError();
        }

        return false;
    }

    private ExchangeFilterFunction resilienceFilter(String integrationName) {

        return (request, next) -> {
            var circuitBreaker =
                    circuitBreakerFactory.create(integrationName);

            return circuitBreaker.run(
                    next.exchange(request), throwable -> {
                        log.error( "Fallback acionado para integração [{}]. Erro: {}",
                                integrationName, throwable.getMessage() );
                        return Mono.error(throwable);
                    }
            );
        };
    }

}
