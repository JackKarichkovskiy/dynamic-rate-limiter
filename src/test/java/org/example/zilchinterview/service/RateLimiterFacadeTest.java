package org.example.zilchinterview.service;

import org.example.zilchinterview.config.RateLimiterAlgorithmConfig;
import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import org.example.zilchinterview.service.algorithm.redis.FixedWindowRateLimiter;
import org.example.zilchinterview.service.algorithm.redis.TokenBucketRateLimiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimiterFacadeTest {

    @Mock
    RateLimiterAlgorithmConfig rateLimiterAlgorithmConfig;
    @Mock
    FixedWindowRateLimiter fixedWindowRateLimiter;
    @Mock
    TokenBucketRateLimiter tokenBucketRateLimiter;

    @InjectMocks
    RateLimiterFacade rateLimiterFacade;

    @Test
    void validateRequest_tokenBucketRateLimiterRoute() {
        // given
        var requestContext = CustomRequestContext.builder().userId("testId").build();
        when(rateLimiterAlgorithmConfig.getRateLimiterAlgorithmName()).thenReturn("TOKEN_BUCKET");
        Mono<RateLimitingResult> rateLimitingResultMono = Mono.just(RateLimitingResult.builder().build());
        when(tokenBucketRateLimiter.validateRequest(requestContext)).thenReturn(rateLimitingResultMono);

        // when
        Mono<RateLimitingResult> resultMono = rateLimiterFacade.validateRequest(requestContext);
        assertThat(resultMono).isSameAs(rateLimitingResultMono);
        verify(tokenBucketRateLimiter).validateRequest(requestContext);
    }

    @Test
    void validateRequest_fixedWindowRateLimiterRoute() {
        // given
        var requestContext = CustomRequestContext.builder().userId("testId").build();
        when(rateLimiterAlgorithmConfig.getRateLimiterAlgorithmName()).thenReturn("FIXED_WINDOW");
        Mono<RateLimitingResult> rateLimitingResultMono = Mono.just(RateLimitingResult.builder().build());
        when(fixedWindowRateLimiter.validateRequest(requestContext)).thenReturn(rateLimitingResultMono);

        // when
        Mono<RateLimitingResult> resultMono = rateLimiterFacade.validateRequest(requestContext);
        assertThat(resultMono).isSameAs(rateLimitingResultMono);
        verify(fixedWindowRateLimiter).validateRequest(requestContext);
    }

    @Test
    void validateRequest_invalidAlgorithmConfig() {
        // given
        var requestContext = CustomRequestContext.builder().userId("testId").build();
        when(rateLimiterAlgorithmConfig.getRateLimiterAlgorithmName()).thenReturn("not_valid");
        Mono<RateLimitingResult> rateLimitingResultMono = Mono.just(RateLimitingResult.builder().build());
        when(tokenBucketRateLimiter.validateRequest(requestContext)).thenReturn(rateLimitingResultMono);

        // when
        Mono<RateLimitingResult> resultMono = rateLimiterFacade.validateRequest(requestContext);
        assertThat(resultMono).isSameAs(rateLimitingResultMono);
        verify(tokenBucketRateLimiter).validateRequest(requestContext);
    }
}
