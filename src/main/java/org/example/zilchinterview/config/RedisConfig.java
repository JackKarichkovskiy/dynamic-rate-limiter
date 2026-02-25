package org.example.zilchinterview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> tokenBucketScript() {
        return RedisScript.of(
                new ClassPathResource("redisScripts/token_bucket.lua"),
                Long.class
        );
    }

    @Bean
    public RedisScript<Long> fixedWindowScript() {
        return RedisScript.of(
                new ClassPathResource("redisScripts/fixed_window.lua"),
                Long.class
        );
    }
}
