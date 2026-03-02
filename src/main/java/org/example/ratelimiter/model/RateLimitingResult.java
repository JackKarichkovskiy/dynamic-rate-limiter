package org.example.ratelimiter.model;

import lombok.Builder;

@Builder
public record RateLimitingResult(boolean allowed) {
}
