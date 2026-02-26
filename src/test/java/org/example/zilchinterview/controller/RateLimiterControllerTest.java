package org.example.zilchinterview.controller;

import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import org.example.zilchinterview.service.RateLimiterFacade;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = RateLimiterController.class)
class RateLimiterControllerTest {

    @MockitoBean
    private RateLimiterFacade rateLimiterFacade;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void rateLimitByUserId_allowed() {
        // given
        when(rateLimiterFacade.validateRequest(any()))
                .thenReturn(Mono.just(RateLimitingResult.builder().allowed(true).build()));

        // when|then
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "testUserId")
                .exchangeSuccessfully();

        ArgumentCaptor<CustomRequestContext> resultArgCaptor = ArgumentCaptor.forClass(CustomRequestContext.class);
        verify(rateLimiterFacade).validateRequest(resultArgCaptor.capture());
        CustomRequestContext usedRequestContext = resultArgCaptor.getValue();
        assertThat(usedRequestContext).isNotNull();
        assertThat(usedRequestContext.userId()).isEqualTo("testUserId");
    }

    @Test
    void rateLimitByUserId_noUserId_allowed() {
        // given
        when(rateLimiterFacade.validateRequest(any()))
                .thenReturn(Mono.just(RateLimitingResult.builder().allowed(true).build()));

        // when|then
        webTestClient.post()
                .uri("/rate-limiter")
                .exchangeSuccessfully();

        ArgumentCaptor<CustomRequestContext> resultArgCaptor = ArgumentCaptor.forClass(CustomRequestContext.class);
        verify(rateLimiterFacade).validateRequest(resultArgCaptor.capture());
        CustomRequestContext usedRequestContext = resultArgCaptor.getValue();
        assertThat(usedRequestContext).isNotNull();
        assertThat(usedRequestContext.userId()).isEqualTo("guest");
    }

    @Test
    void rateLimitByUserId_forbidden() {
        // given
        when(rateLimiterFacade.validateRequest(any()))
                .thenReturn(Mono.just(RateLimitingResult.builder().allowed(false).build()));

        // when|then
        webTestClient.post()
                .uri("/rate-limiter")
                .header("user-id", "testUserId")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

        ArgumentCaptor<CustomRequestContext> resultArgCaptor = ArgumentCaptor.forClass(CustomRequestContext.class);
        verify(rateLimiterFacade).validateRequest(resultArgCaptor.capture());
        CustomRequestContext usedRequestContext = resultArgCaptor.getValue();
        assertThat(usedRequestContext).isNotNull();
        assertThat(usedRequestContext.userId()).isEqualTo("testUserId");
    }
}
