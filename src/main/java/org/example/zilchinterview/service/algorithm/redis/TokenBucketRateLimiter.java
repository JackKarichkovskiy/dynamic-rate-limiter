package org.example.zilchinterview.service.algorithm.redis;

import lombok.extern.slf4j.Slf4j;
import org.example.zilchinterview.config.TokenBucketAlgorithmProperties;
import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import org.example.zilchinterview.service.algorithm.RateLimiterAlgorithmStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
public class TokenBucketRateLimiter implements RateLimiterAlgorithmStrategy {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<Long> redisScript;
    private final TokenBucketAlgorithmProperties algorithmProperties;
    private final Supplier<Long> nowMillisSupplier;

    public TokenBucketRateLimiter(ReactiveStringRedisTemplate redisTemplate,
                                  @Qualifier("tokenBucketScript") RedisScript<Long> redisScript,
                                  TokenBucketAlgorithmProperties algorithmProperties,
                                  Supplier<Long> nowMillisSupplier) {
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
        this.algorithmProperties = algorithmProperties;
        this.nowMillisSupplier = nowMillisSupplier;
    }

    @Override
    public Mono<RateLimitingResult> validateRequest(CustomRequestContext context) {
        log.info("Validating request for context={}", context);

        String keySuffix = "user_id:" + context.userId();
        long now = nowMillisSupplier.get();

        return redisTemplate.execute(
                redisScript,
                List.of("rate_limit:token_bucket:" + keySuffix),
                String.valueOf(algorithmProperties.getRpm()),
                String.valueOf(now)
        ).next().map(result -> RateLimitingResult.builder().allowed(result == 1).build());
    }
}
