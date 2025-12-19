package com.caderneta.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ResilienceConfig {

    private final ExternalApiConfig externalApiConfig;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerCustomizer() {
        return factory -> externalApiConfig.getApi()
                .forEach((product, config) -> {
                    CircuitBreakerConfig circuitBreakerConfig = getCircuitBreakerConfig(config);
                    TimeLimiterConfig timeLimiterConfig = getTimeLimiterConfig(config);

                    // Também registra diretamente no registry para garantir a configuração
                    try {
                        circuitBreakerRegistry.circuitBreaker(product, circuitBreakerConfig);
                    } catch (Exception ex) {
                        log.warn("Não foi possível registrar circuit breaker '{}' no registry: {}", product, ex.getMessage());
                    }

                    // Aplica tanto o CircuitBreakerConfig quanto o TimeLimiterConfig para o product
                    factory.configure(builder ->
                            builder.circuitBreakerConfig(circuitBreakerConfig)
                            .timeLimiterConfig(timeLimiterConfig), product);
                });
    }

    private static CircuitBreakerConfig getCircuitBreakerConfig(ExternalApiConfig.ProductConfig config) {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(
                        config.getCircuitBreaker().getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(5000))
                .slowCallRateThreshold(config.getCircuitBreaker().getSlowCallRateThreshold())
                .slowCallDurationThreshold(
                        Duration.ofMillis(
                                config.getCircuitBreaker().getSlowCallDurationThreshold()))
                .minimumNumberOfCalls(10)
                .slidingWindowType(
                        CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .slidingWindowSize(10)
                .permittedNumberOfCallsInHalfOpenState(
                        config.getCircuitBreaker()
                                .getPermittedCallsInHalfOpenState())
                .build();
    }

    private static TimeLimiterConfig getTimeLimiterConfig(ExternalApiConfig.ProductConfig config) {
        // Ajusta o timeout do TimeLimiter para cobrir as tentativas de retry
        long baseTimeoutMs = config.getRequestTimeout();
        long additionalRetryBackoffMs = 0L;
        try {
            if (config.getRetry() != null) {
                additionalRetryBackoffMs = (long) config.getRetry().getBackoffMs() * (long) config.getRetry().getMaxAttempts();
            }
        } catch (Exception ignored) {
        }

        long totalTimeoutMs = baseTimeoutMs + additionalRetryBackoffMs;
        return TimeLimiterConfig.custom()
                .timeoutDuration(
                        Duration.ofMillis(totalTimeoutMs))
                .build();
    }
}
