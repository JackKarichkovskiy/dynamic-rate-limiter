package org.example.zilchinterview.config;

import org.example.zilchinterview.service.algorithm.CircuitBreakerRateLimiter;
import org.example.zilchinterview.service.algorithm.RateLimiterAlgorithmStrategy;
import org.example.zilchinterview.service.algorithm.redis.FixedWindowRateLimiter;
import org.example.zilchinterview.service.algorithm.redis.TokenBucketRateLimiter;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterAlgorithmsConfig {

    @Bean
    public RateLimiterAlgorithmStrategy resilientFixedWindowStrategy(
            FixedWindowRateLimiter fixedWindowRateLimiter, ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        ReactiveCircuitBreaker fixedWindowCircuitBreaker = circuitBreakerFactory.create("fixedWindowStrategy");
        return new CircuitBreakerRateLimiter(fixedWindowRateLimiter, fixedWindowCircuitBreaker);
    }

    @Bean
    public RateLimiterAlgorithmStrategy resilientTokenBucketStrategy(
            TokenBucketRateLimiter tokenBucketRateLimiter, ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        ReactiveCircuitBreaker tokenBucketCircuitBreaker = circuitBreakerFactory.create("tokenBucketStrategy");
        return new CircuitBreakerRateLimiter(tokenBucketRateLimiter, tokenBucketCircuitBreaker);
    }
}
