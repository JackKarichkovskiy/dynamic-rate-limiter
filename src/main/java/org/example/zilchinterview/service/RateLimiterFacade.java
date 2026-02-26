package org.example.zilchinterview.service;

import lombok.extern.slf4j.Slf4j;
import org.example.zilchinterview.config.RateLimiterDynamicAlgorithmName;
import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingAlgorithm;
import org.example.zilchinterview.model.RateLimitingResult;
import org.example.zilchinterview.service.algorithm.RateLimiterAlgorithmStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RateLimiterFacade {

    private static final RateLimitingAlgorithm DEFAULT_ALGORITHM = RateLimitingAlgorithm.TOKEN_BUCKET;

    private final RateLimiterDynamicAlgorithmName rateLimiterDynamicAlgorithmName;
    private final RateLimiterAlgorithmStrategy fixedWindowRateLimiter;
    private final RateLimiterAlgorithmStrategy tokenBucketRateLimiter;

    public RateLimiterFacade(RateLimiterDynamicAlgorithmName rateLimiterDynamicAlgorithmName,
                             @Qualifier("resilientFixedWindowStrategy") RateLimiterAlgorithmStrategy fixedWindowRateLimiter,
                             @Qualifier("resilientTokenBucketStrategy") RateLimiterAlgorithmStrategy tokenBucketRateLimiter) {
        this.rateLimiterDynamicAlgorithmName = rateLimiterDynamicAlgorithmName;
        this.fixedWindowRateLimiter = fixedWindowRateLimiter;
        this.tokenBucketRateLimiter = tokenBucketRateLimiter;
    }

    public Mono<RateLimitingResult> validateRequest(CustomRequestContext context) {
        String algorithmName = rateLimiterDynamicAlgorithmName.getRateLimiterAlgorithmName();
        RateLimiterAlgorithmStrategy rateLimiterAlgorithm = selectRateLimiterAlgorithm(algorithmName);
        return rateLimiterAlgorithm.validateRequest(context);
    }

    private RateLimiterAlgorithmStrategy selectRateLimiterAlgorithm(String algorithmName) {
        RateLimitingAlgorithm algorithm;
        try {
            algorithm = RateLimitingAlgorithm.valueOf(algorithmName);
        } catch (IllegalArgumentException ex) {
            log.warn("{} is not a valid rate-limiter-algorithm. Using the default", algorithmName);
            algorithm = DEFAULT_ALGORITHM;
        }
        return switch (algorithm) {
            case TOKEN_BUCKET -> tokenBucketRateLimiter;
            case FIXED_WINDOW -> fixedWindowRateLimiter;
        };
    }
}
