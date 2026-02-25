package org.example.zilchinterview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import org.example.zilchinterview.service.RateLimiterFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/rate-limiter")
public class RateLimiterController {

    private final RateLimiterFacade rateLimiterFacade;

    @PostMapping
    public Mono<ResponseEntity<?>> rateLimitByUserId(
            @RequestHeader(value = "user-id", defaultValue = "guest") String userId) {
        var requestContext = CustomRequestContext.builder().userId(userId).build();
        return rateLimiterFacade.validateRequest(requestContext)
                .map(this::mapToRateLimiterResponse);
    }

    private ResponseEntity<?> mapToRateLimiterResponse(RateLimitingResult result) {
        return result.allowed()
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
