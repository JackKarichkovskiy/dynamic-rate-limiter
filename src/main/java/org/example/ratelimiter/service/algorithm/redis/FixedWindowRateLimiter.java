package org.example.ratelimiter.service.algorithm.redis;

import lombok.extern.slf4j.Slf4j;
import org.example.ratelimiter.config.FixedWindowAlgorithmProperties;
import org.example.ratelimiter.model.CustomRequestContext;
import org.example.ratelimiter.model.RateLimitingResult;
import org.example.ratelimiter.service.algorithm.RateLimiterAlgorithmStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
public class FixedWindowRateLimiter implements RateLimiterAlgorithmStrategy {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RedisScript<Long> redisScript;
    private final FixedWindowAlgorithmProperties algorithmProperties;
    private final Supplier<Long> nowMillisSupplier;

    public FixedWindowRateLimiter(ReactiveStringRedisTemplate redisTemplate,
                                  @Qualifier("fixedWindowScript") RedisScript<Long> redisScript,
                                  FixedWindowAlgorithmProperties algorithmProperties,
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
                List.of("rate_limit:fixed_window:" + keySuffix),
                String.valueOf(algorithmProperties.getRpm()),
                String.valueOf(now)
        ).next().map(result -> RateLimitingResult.builder().allowed(result == 1).build());
    }
}
