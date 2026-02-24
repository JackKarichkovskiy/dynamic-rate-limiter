package org.example.zilchinterview.service.algorithm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import org.springframework.data.redis.connection.RedisHashCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBucketRateLimiter implements RateLimiterImpl {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Override
    public Mono<RateLimitingResult> validateRequest(CustomRequestContext context) {
        log.info("TokenBucketRateLimiter validateRequest");
        return redisTemplate.opsForHash().putAndExpire("person", Map.of(
                "first_name", "Yevhen",
                "last_name", "Test"
        ), RedisHashCommands.HashFieldSetOption.UPSERT, Expiration.seconds(10))
                .thenReturn(RateLimitingResult.builder().success(true).build());
    }
}
