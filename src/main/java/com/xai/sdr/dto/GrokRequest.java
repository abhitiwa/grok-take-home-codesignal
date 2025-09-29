package com.xai.sdr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * DTO representing a request to the Grok API.
 * 
 * Contains the message structure and parameters needed
 * for communicating with the Grok chat completions endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrokRequest {
    
    @JsonProperty("messages")
    private List<GrokMessage> messages;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("temperature")
    private Double temperature;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    
    @JsonProperty("stream")
    private Boolean stream;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GrokMessage {
        
        @JsonProperty("role")
        private String role;
        
        @JsonProperty("content")
        private String content;
    }
}