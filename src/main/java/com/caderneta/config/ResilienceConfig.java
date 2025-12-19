package com.caderneta.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class ResilienceConfig {

    private final ExternalApiConfig externalApiConfig;

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerCustomizer() {
        return factory -> externalApiConfig.getApi()
                .forEach((product, config) -> factory.configure(builder -> {
                    CircuitBreakerConfig circuitBreakerConfig = getCircuitBreakerConfig(config);
                    TimeLimiterConfig timeLimiterConfig = getTimeLimiterConfig(config);

                    builder
                            .circuitBreakerConfig(circuitBreakerConfig)
                            .timeLimiterConfig(timeLimiterConfig);

                }, product));
    }

    private static CircuitBreakerConfig getCircuitBreakerConfig(ExternalApiConfig.ProductConfig config) {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(
                        config.getCircuitBreaker().getFailureRateThreshold())
                .slowCallRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(5000))
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
        return TimeLimiterConfig.custom()
                .timeoutDuration(
                        Duration.ofMillis(config.getRequestTimeout()))
                .build();
    }
}
