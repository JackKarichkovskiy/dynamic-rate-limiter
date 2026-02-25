package org.example.zilchinterview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.rate-limiter.token-bucket")
public class TokenBucketAlgorithmProperties {
    private int rpm;
}
