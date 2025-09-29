package com.xai.sdr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * DTO representing a response from the Grok API.
 * 
 * Contains the generated content and metadata returned
 * from the Grok chat completions endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrokResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("object")
    private String object;
    
    @JsonProperty("created")
    private Long created;
    
    @JsonProperty("model")
    private String model;
    
    @JsonProperty("choices")
    private List<GrokChoice> choices;
    
    @JsonProperty("usage")
    private GrokUsage usage;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GrokChoice {
        
        @JsonProperty("index")
        private Integer index;
        
        @JsonProperty("message")
        private GrokMessage message;
        
        @JsonProperty("finish_reason")
        private String finishReason;
    }
    
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
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GrokUsage {
        
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
    
    /**
     * Get the content from the first choice
     */
    public String getContent() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }
}