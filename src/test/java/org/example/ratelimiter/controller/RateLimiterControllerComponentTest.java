package org.example.ratelimiter.controller;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class RateLimiterControllerComponentTest {

    @Container
    static RedisContainer redis = new RedisContainer(
            RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

    @Autowired
    WebTestClient webTestClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getRedisPort);
    }

    @Test
    void rateLimitByUserId_firstRequest_allowed() {
        // when|then
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "rateLimitByUserId_firstRequest_allowed")
                .exchangeSuccessfully();
    }

    @Test
    void rateLimitByUserId_limitReached_forbidden() {
        // when|then
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "rateLimitByUserId_limitReached_forbidden")
                .exchangeSuccessfully();
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "rateLimitByUserId_limitReached_forbidden")
                .exchangeSuccessfully();
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "rateLimitByUserId_limitReached_forbidden")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    void rateLimitByUserId_limitReachedButDifferentUsers_allowed() {
        // when|then
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "rateLimitByUserId_limitReachedButDifferentUsers_allowed1")
                .exchangeSuccessfully();
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "rateLimitByUserId_limitReachedButDifferentUsers_allowed1")
                .exchangeSuccessfully();
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "rateLimitByUserId_limitReachedButDifferentUsers_allowed2")
                .exchangeSuccessfully();
    }
}
