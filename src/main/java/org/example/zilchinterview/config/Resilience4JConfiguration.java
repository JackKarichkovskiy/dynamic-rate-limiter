package org.example.zilchinterview.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfiguration {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }
}
