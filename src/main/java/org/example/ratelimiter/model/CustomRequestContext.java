package org.example.ratelimiter.model;

import lombok.Builder;

@Builder
public record CustomRequestContext(String userId) {
}
