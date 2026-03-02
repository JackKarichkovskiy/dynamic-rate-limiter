package org.example.ratelimiter.service.algorithm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ratelimiter.model.CustomRequestContext;
import org.example.ratelimiter.model.RateLimitingResult;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class CircuitBreakerRateLimiter implements RateLimiterAlgorithmStrategy {

    private final RateLimiterAlgorithmStrategy delegate;
    private final ReactiveCircuitBreaker rateLimiterCircuitBreaker;

    @Override
    public Mono<RateLimitingResult> validateRequest(CustomRequestContext context) {
        return rateLimiterCircuitBreaker.run(delegateCall(context), this::fallback);
    }

    private Mono<RateLimitingResult> delegateCall(CustomRequestContext context) {
        return Mono.defer(() -> delegate.validateRequest(context));
    }

    private Mono<RateLimitingResult> fallback(Throwable ex) {
        log.error("Rate limiter delegate exception. Returning fallback", ex);
        return Mono.just(RateLimitingResult.builder().allowed(true).build());
    }
}
