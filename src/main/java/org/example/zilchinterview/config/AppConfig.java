package org.example.zilchinterview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class AppConfig {

    @Bean
    public Supplier<Long> nowMillisSupplier() {
        return System::currentTimeMillis;
    }
}
