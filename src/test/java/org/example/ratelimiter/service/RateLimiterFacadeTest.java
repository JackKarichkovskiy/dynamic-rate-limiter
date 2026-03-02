package org.example.ratelimiter.service;

import org.example.ratelimiter.config.RateLimiterDynamicAlgorithmName;
import org.example.ratelimiter.model.CustomRequestContext;
import org.example.ratelimiter.model.RateLimitingResult;
import org.example.ratelimiter.service.algorithm.RateLimiterAlgorithmStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimiterFacadeTest {

    @Mock
    RateLimiterDynamicAlgorithmName rateLimiterDynamicAlgorithmName;
    @Mock
    RateLimiterAlgorithmStrategy fixedWindowRateLimiter;
    @Mock
    RateLimiterAlgorithmStrategy tokenBucketRateLimiter;

    RateLimiterFacade rateLimiterFacade;

    @BeforeEach
    void beforeEach() {
        rateLimiterFacade = new RateLimiterFacade(rateLimiterDynamicAlgorithmName, fixedWindowRateLimiter,
                tokenBucketRateLimiter);
    }

    @Test
    void validateRequest_tokenBucketRateLimiterRoute() {
        // given
        var requestContext = CustomRequestContext.builder().userId("testId").build();
        when(rateLimiterDynamicAlgorithmName.getRateLimiterAlgorithmName()).thenReturn("TOKEN_BUCKET");
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
        when(rateLimiterDynamicAlgorithmName.getRateLimiterAlgorithmName()).thenReturn("FIXED_WINDOW");
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
        when(rateLimiterDynamicAlgorithmName.getRateLimiterAlgorithmName()).thenReturn("not_valid");
        Mono<RateLimitingResult> rateLimitingResultMono = Mono.just(RateLimitingResult.builder().build());
        when(tokenBucketRateLimiter.validateRequest(requestContext)).thenReturn(rateLimitingResultMono);

        // when
        Mono<RateLimitingResult> resultMono = rateLimiterFacade.validateRequest(requestContext);
        assertThat(resultMono).isSameAs(rateLimitingResultMono);
        verify(tokenBucketRateLimiter).validateRequest(requestContext);
    }
}
