package com.caderneta.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "integration")
public class ExternalApiConfig {
    private Map<String, ProductConfig> api = new HashMap<>();

    @Getter
    @Setter
    public static class ProductConfig {
        private String baseUrl;
        private int requestTimeout;
        private int connectionTimeout;
        private retryConfig retry;
        private CircuitBreakerConfig circuitBreaker;

        @Getter
        @Setter
        public static class CircuitBreakerConfig {
            private int failureRateThreshold;
            private int slowCallDurationThreshold;
            private int slowCallRateThreshold;
            private int permittedCallsInHalfOpenState;
        }

        @Getter
        @Setter
        public static class retryConfig {
            private int maxAttempts;
            private long backoffMs;
            private Double jitter;
        }
    }
}
