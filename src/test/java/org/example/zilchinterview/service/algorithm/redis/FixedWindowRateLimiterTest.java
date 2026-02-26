package org.example.zilchinterview.service.algorithm.redis;

import com.redis.testcontainers.RedisContainer;
import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"app.rate-limiter.fixed-window.rpm=2"})
@Testcontainers
class FixedWindowRateLimiterTest {

    private static final long MINUTE_IN_MILLIS = 1000 * 60;

    @Container
    static RedisContainer redis = new RedisContainer(
            RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

    @MockitoBean
    Supplier<Long> nowMillisSupplier;

    @Autowired
    FixedWindowRateLimiter fixedWindowRateLimiter;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getRedisPort);
    }

    @Test
    void validateRequest_firstRequest_allowed() {
        // given
        var requestContext = CustomRequestContext.builder().userId("validateRequest_firstRequest_allowed").build();
        Mockito.when(nowMillisSupplier.get()).thenReturn(100L);

        // when
        Mono<RateLimitingResult> resultMono = fixedWindowRateLimiter.validateRequest(requestContext);

        // then
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.allowed()).isTrue();
                }).verifyComplete();
    }

    @Test
    void validateRequest_rpmReached_forbidden() {
        // given
        var requestContext = CustomRequestContext.builder().userId("validateRequest_rpmReached_forbidden").build();
        Mockito.when(nowMillisSupplier.get()).thenReturn(100L, 101L, 102L);

        for (int i = 0; i < 2; i++) {
            // when
            Mono<RateLimitingResult> resultMono = fixedWindowRateLimiter.validateRequest(requestContext);

            // then
            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.allowed()).isTrue();
                    }).verifyComplete();
        }

        // when
        Mono<RateLimitingResult> resultMono = fixedWindowRateLimiter.validateRequest(requestContext);

        // then
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.allowed()).isFalse();
                }).verifyComplete();
    }

    @Test
    void validateRequest_rpmReachedButOnMinuteEdges_allowed() {
        // given
        var requestContext = CustomRequestContext.builder()
                .userId("validateRequest_rpmReachedButOnMinuteEdges_allowed").build();
        Mockito.when(nowMillisSupplier.get()).thenReturn(MINUTE_IN_MILLIS - 1, MINUTE_IN_MILLIS - 1,
                MINUTE_IN_MILLIS + 1, MINUTE_IN_MILLIS + 1);

        for (int i = 0; i < 4; i++) {
            // when
            Mono<RateLimitingResult> resultMono = fixedWindowRateLimiter.validateRequest(requestContext);

            // then
            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.allowed()).isTrue();
                    }).verifyComplete();
        }
    }

    @Test
    void validateRequest_lastRequestInNextMinute_allowed() {
        // given
        var requestContext = CustomRequestContext.builder().userId("validateRequest_lastRequestInNextMinute_allowed").build();
        Mockito.when(nowMillisSupplier.get()).thenReturn(100L, 101L, 100L + MINUTE_IN_MILLIS);

        for (int i = 0; i < 3; i++) {
            // when
            Mono<RateLimitingResult> resultMono = fixedWindowRateLimiter.validateRequest(requestContext);

            // then
            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.allowed()).isTrue();
                    }).verifyComplete();
        }
    }

    @Test
    void validateRequest_differentUserId_allowed() {
        // given
        var requestContext1 = CustomRequestContext.builder().userId("validateRequest_differentUserId_allowed1").build();
        var requestContext2 = CustomRequestContext.builder().userId("validateRequest_differentUserId_allowed2").build();
        Mockito.when(nowMillisSupplier.get()).thenReturn(100L, 101L, 100L, 101L);

        for (int i = 0; i < 2; i++) {
            // when
            Mono<RateLimitingResult> resultMono = fixedWindowRateLimiter.validateRequest(requestContext1);

            // then
            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.allowed()).isTrue();
                    }).verifyComplete();
        }

        for (int i = 0; i < 2; i++) {
            // when
            Mono<RateLimitingResult> resultMono = fixedWindowRateLimiter.validateRequest(requestContext2);

            // then
            StepVerifier.create(resultMono)
                    .assertNext(result -> {
                        assertThat(result).isNotNull();
                        assertThat(result.allowed()).isTrue();
                    }).verifyComplete();
        }
    }
}
