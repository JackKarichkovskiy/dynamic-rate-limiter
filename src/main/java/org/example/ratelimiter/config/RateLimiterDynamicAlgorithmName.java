package org.example.ratelimiter.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Getter
@Component
@RefreshScope
public class RateLimiterDynamicAlgorithmName {

    private final String rateLimiterAlgorithmName;

    public RateLimiterDynamicAlgorithmName(@Value("${app.rate-limiter-algorithm}") String rateLimiterAlgorithmName) {
        this.rateLimiterAlgorithmName = rateLimiterAlgorithmName;
    }
}
