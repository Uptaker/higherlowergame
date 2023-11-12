package com.tammeoja.higherlower.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BeansConfiguration {

  @Bean
  Clock clock() {
    return Clock.systemDefaultZone();
  }
}