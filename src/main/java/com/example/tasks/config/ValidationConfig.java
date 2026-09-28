package com.example.tasks.config;

import java.time.Clock;
import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public ValidationConfigurationCustomizer validationClock(Clock clock) {
    return configuration -> configuration.clockProvider(() -> clock);
  }
}
