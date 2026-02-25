package org.example.zilchinterview.model;

import lombok.Builder;

@Builder
public record RateLimitingResult(boolean allowed) {
}
