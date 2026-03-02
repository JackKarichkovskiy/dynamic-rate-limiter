package org.example.ratelimiter;

import org.springframework.boot.SpringApplication;

public class TestDynamicRateLimiterApplication {

    static void main(String[] args) {
        SpringApplication.from(DynamicRateLimiterApplication::main).run(args);
    }

}
