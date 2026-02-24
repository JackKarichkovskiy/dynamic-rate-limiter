package org.example.zilchinterview.service.algorithm;

import org.example.zilchinterview.model.CustomRequestContext;
import org.example.zilchinterview.model.RateLimitingResult;
import reactor.core.publisher.Mono;

public interface RateLimiterImpl {

    Mono<RateLimitingResult> validateRequest(CustomRequestContext context);
}
