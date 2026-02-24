package org.example.zilchinterview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zilchinterview.config.RateLimiterAlgorithmConfig;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rate-limiter")
public class RateLimiterController {

    private final RateLimiterAlgorithmConfig rateLimiterAlgorithmConfig;

    @PostMapping
    public Mono<String> rateLimitByUserId(
            @RequestHeader(value = "user-id", defaultValue = "guest") String userId) {
        log.info("user-id={}", userId);
        log.info("algorithm={}", rateLimiterAlgorithmConfig.getRateLimiterAlgorithmName());

        return Mono.just("OK");
    }
}
