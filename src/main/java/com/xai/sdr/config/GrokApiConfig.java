package com.xai.sdr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Grok API integration.
 * 
 * These properties are loaded from application.yml and can be overridden
 * via environment variables for different deployment environments.
 */
@Configuration
@ConfigurationProperties(prefix = "grok.api")
public class GrokApiConfig {
    
    private String baseUrl = "https://api.x.ai/v1";
    private String model = "grok-4";
    private double temperature = 0.7;
    private int maxTokens = 1000;
    private int timeout = 30000;
    
    public GrokApiConfig() {}
    
    // Getters and Setters
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
