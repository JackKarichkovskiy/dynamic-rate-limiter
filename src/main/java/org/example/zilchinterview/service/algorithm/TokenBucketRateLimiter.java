package org.example.zilchinterview.service.algorithm;

import lombok.extern.slf4j.Slf4j;
import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TokenBucketRateLimiter implements RateLimiterImpl {

    @Override
    public Mono<RateLimitingResult> validateRequest(CustomRequestContext context) {
        log.info("TokenBucketRateLimiter validateRequest");
        return Mono.just(RateLimitingResult.builder().build());
    }
}
