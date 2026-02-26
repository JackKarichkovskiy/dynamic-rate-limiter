package org.example.zilchinterview.service.algorithm;

import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CircuitBreakerRateLimiterTest {

    @Autowired
    ReactiveCircuitBreakerFactory circuitBreakerFactory;

    RateLimiterAlgorithmStrategy delegate = mock(RateLimiterAlgorithmStrategy.class);

    CircuitBreakerRateLimiter circuitBreakerRateLimiter;

    @Test
    void validateRequest_happyCase() {
        // given
        ReactiveCircuitBreaker circuitBreaker = circuitBreakerFactory.create("validateRequest_happyCase");
        circuitBreakerRateLimiter = new CircuitBreakerRateLimiter(delegate, circuitBreaker);

        var requestContext = CustomRequestContext.builder().build();
        var limitingResult = RateLimitingResult.builder().build();
        when(delegate.validateRequest(requestContext)).thenReturn(Mono.just(limitingResult));

        // when
        Mono<RateLimitingResult> resultMono = circuitBreakerRateLimiter.validateRequest(requestContext);

        // then
        StepVerifier.create(resultMono)
                .assertNext(result -> assertThat(result).isSameAs(limitingResult))
                .verifyComplete();
    }

    @Test
    void validateRequest_circuitBreakerOpen() {
        // given
        ReactiveCircuitBreaker circuitBreaker = circuitBreakerFactory.create("validateRequest_circuitBreakerOpen");
        circuitBreakerRateLimiter = new CircuitBreakerRateLimiter(delegate, circuitBreaker);

        var requestContext = CustomRequestContext.builder().build();
        when(delegate.validateRequest(requestContext)).thenReturn(Mono.error(new RuntimeException("test error")));

        for (int i = 0; i < 10; i++) {
            // when
            Mono<RateLimitingResult> resultMono = circuitBreakerRateLimiter.validateRequest(requestContext);

            // then
            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.allowed()).isTrue();
                    })
                    .verifyComplete();
        }
        verify(delegate, times(5)).validateRequest(requestContext);
    }
}
