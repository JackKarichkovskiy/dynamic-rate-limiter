package org.example.zilchinterview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zilchinterview.config.RateLimiterAlgorithmConfig;
import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingAlgorithm;
import org.example.zilchinterview.model.RateLimitingResult;
import org.example.zilchinterview.service.algorithm.FixedWindowRateLimiter;
import org.example.zilchinterview.service.algorithm.RateLimiterImpl;
import org.example.zilchinterview.service.algorithm.TokenBucketRateLimiter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterFacade {

    private static final RateLimitingAlgorithm DEFAULT_ALGORITHM = RateLimitingAlgorithm.TOKEN_BUCKET;

    private final RateLimiterAlgorithmConfig rateLimiterAlgorithmConfig;
    private final FixedWindowRateLimiter fixedWindowRateLimiter;
    private final TokenBucketRateLimiter tokenBucketRateLimiter;

    public Mono<RateLimitingResult> validateRequest(CustomRequestContext context) {
        String algorithmName = rateLimiterAlgorithmConfig.getRateLimiterAlgorithmName();
        RateLimiterImpl rateLimiterAlgorithm = getRateLimiterAlgorithm(algorithmName);
        return rateLimiterAlgorithm.validateRequest(context);
    }

    private RateLimiterImpl getRateLimiterAlgorithm(String algorithmName) {
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
