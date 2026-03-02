package org.example.ratelimiter.service.algorithm;

import org.example.ratelimiter.model.CustomRequestContext;
import org.example.ratelimiter.model.RateLimitingResult;
import reactor.core.publisher.Mono;

/**
 * Interface that abstracts supported rate limiter algorithms implementations.
 */
public interface RateLimiterAlgorithmStrategy {

    /**
     * Validates request based on the request context.
     *
     * @return decision result whether to allow request or not
     */
    Mono<RateLimitingResult> validateRequest(CustomRequestContext context);
}
