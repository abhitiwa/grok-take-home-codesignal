package com.xai.sdr.service;

import com.xai.sdr.config.GrokApiConfig;
import com.xai.sdr.dto.GrokRequest;
import com.xai.sdr.dto.GrokResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Service for interacting with the Grok API.
 * 
 * Handles all communication with the Grok chat completions endpoint,
 * including request formatting, error handling, and response processing.
 */
@Service
@Slf4j
public class GrokApiService {
    
    private final GrokApiConfig grokConfig;
    private final WebClient webClient;
    
    @Autowired
    public GrokApiService(GrokApiConfig grokConfig) {
        this.grokConfig = grokConfig;
        this.webClient = WebClient.builder()
                .baseUrl(grokConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    /**
     * Send a chat completion request to Grok API
     */
    public String sendChatCompletion(String userMessage) {
        return sendChatCompletion(userMessage, grokConfig.getTemperature());
    }
    
    /**
     * Send a chat completion request with custom temperature
     */
    public String sendChatCompletion(String userMessage, double temperature) {
        try {
            GrokRequest request = GrokRequest.builder()
                    .messages(Collections.singletonList(
                            GrokRequest.GrokMessage.builder()
                                    .role("user")
                                    .content(userMessage)
                                    .build()
                    ))
                    .model(grokConfig.getModel())
                    .temperature(temperature)
                    .maxTokens(grokConfig.getMaxTokens())
                    .stream(false)
                    .build();
            
            GrokResponse response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + getApiKey())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GrokResponse.class)
                    .timeout(Duration.ofMillis(grokConfig.getTimeout()))
                    .block();
            
            if (response != null && response.getContent() != null) {
                log.info("Grok API response received successfully");
                return response.getContent();
            } else {
                log.error("Empty or invalid response from Grok API");
                throw new RuntimeException("Empty response from Grok API");
            }
            
        } catch (WebClientResponseException e) {
            log.error("Grok API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to communicate with Grok API: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error calling Grok API", e);
            throw new RuntimeException("Unexpected error calling Grok API: " + e.getMessage());
        }
    }
    
    /**
     * Send a conversation with multiple messages
     */
    public String sendConversation(List<GrokRequest.GrokMessage> messages) {
        try {
            GrokRequest request = GrokRequest.builder()
                    .messages(messages)
                    .model(grokConfig.getModel())
                    .temperature(grokConfig.getTemperature())
                    .maxTokens(grokConfig.getMaxTokens())
                    .stream(false)
                    .build();
            
            GrokResponse response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + getApiKey())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GrokResponse.class)
                    .timeout(Duration.ofMillis(grokConfig.getTimeout()))
                    .block();
            
            if (response != null && response.getContent() != null) {
                log.info("Grok API conversation response received successfully");
                return response.getContent();
            } else {
                log.error("Empty or invalid conversation response from Grok API");
                throw new RuntimeException("Empty response from Grok API");
            }
            
        } catch (WebClientResponseException e) {
            log.error("Grok API conversation error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to communicate with Grok API: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error calling Grok API for conversation", e);
            throw new RuntimeException("Unexpected error calling Grok API: " + e.getMessage());
        }
    }
    
    /**
     * Test the Grok API connection
     */
    public boolean testConnection() {
        try {
            String response = sendChatCompletion("Hello, this is a test message. Please respond with 'Connection successful'.");
            return response != null && response.toLowerCase().contains("connection successful");
        } catch (Exception e) {
            log.error("Grok API connection test failed", e);
            return false;
        }
    }
    
    /**
     * Get API key from environment variable
     */
    private String getApiKey() {
        String apiKey = System.getenv("XAI_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("XAI_API_KEY environment variable is not set");
        }
        return apiKey;
    }
}