package com.caderneta.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final ExternalApiConfig externalApiConfig;
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
                        entry -> webClientBuilder
                                .clone()
                                .clientConnector(new ReactorClientHttpConnector(createHttpClient(entry.getValue().getRequestTimeout(), entry.getValue().getConnectionTimeout())))
                                .baseUrl(entry.getValue().getBaseUrl())
                                .build()
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

}
