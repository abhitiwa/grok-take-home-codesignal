package com.xai.sdr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO representing a request to the Grok API.
 * 
 * Contains the message structure and parameters needed
 * for communicating with the Grok chat completions endpoint.
 */
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
    
    public GrokRequest() {}
    
    public GrokRequest(List<GrokMessage> messages, String model, Double temperature, Integer maxTokens, Boolean stream) {
        this.messages = messages;
        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.stream = stream;
    }
    
    public static GrokRequestBuilder builder() {
        return new GrokRequestBuilder();
    }
    
    // Getters and Setters
    public List<GrokMessage> getMessages() { return messages; }
    public void setMessages(List<GrokMessage> messages) { this.messages = messages; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    
    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }
    
    public static class GrokMessage {
        
        @JsonProperty("role")
        private String role;
        
        @JsonProperty("content")
        private String content;
        
        public GrokMessage() {}
        
        public GrokMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public static GrokMessageBuilder builder() {
            return new GrokMessageBuilder();
        }
        
        // Getters and Setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    // Builder classes
    public static class GrokRequestBuilder {
        private List<GrokMessage> messages;
        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Boolean stream;
        
        public GrokRequestBuilder messages(List<GrokMessage> messages) {
            this.messages = messages;
            return this;
        }
        
        public GrokRequestBuilder model(String model) {
            this.model = model;
            return this;
        }
        
        public GrokRequestBuilder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }
        
        public GrokRequestBuilder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }
        
        public GrokRequestBuilder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }
        
        public GrokRequest build() {
            return new GrokRequest(messages, model, temperature, maxTokens, stream);
        }
    }
    
    public static class GrokMessageBuilder {
        private String role;
        private String content;
        
        public GrokMessageBuilder role(String role) {
            this.role = role;
            return this;
        }
        
        public GrokMessageBuilder content(String content) {
            this.content = content;
            return this;
        }
        
        public GrokMessage build() {
            return new GrokMessage(role, content);
        }
    }
}