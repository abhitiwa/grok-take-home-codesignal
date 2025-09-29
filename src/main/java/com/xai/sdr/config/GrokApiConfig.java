package com.xai.sdr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Grok API integration.
 * 
 * These properties are loaded from application.yml and can be overridden
 * via environment variables for different deployment environments.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "grok.api")
public class GrokApiConfig {
    
    private String baseUrl = "https://api.x.ai/v1";
    private String model = "grok-4";
    private double temperature = 0.7;
    private int maxTokens = 1000;
    private int timeout = 30000;
}