package com.xai.sdr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO representing a response from the Grok API.
 * 
 * Contains the generated content and metadata returned
 * from the Grok chat completions endpoint.
 */
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
    
    public GrokResponse() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }
    
    public Long getCreated() { return created; }
    public void setCreated(Long created) { this.created = created; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public List<GrokChoice> getChoices() { return choices; }
    public void setChoices(List<GrokChoice> choices) { this.choices = choices; }
    
    public GrokUsage getUsage() { return usage; }
    public void setUsage(GrokUsage usage) { this.usage = usage; }
    
    /**
     * Get the content from the first choice
     */
    public String getContent() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }
    
    public static class GrokChoice {
        
        @JsonProperty("index")
        private Integer index;
        
        @JsonProperty("message")
        private GrokMessage message;
        
        @JsonProperty("finish_reason")
        private String finishReason;
        
        public GrokChoice() {}
        
        // Getters and Setters
        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }
        
        public GrokMessage getMessage() { return message; }
        public void setMessage(GrokMessage message) { this.message = message; }
        
        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
    }
    
    public static class GrokMessage {
        
        @JsonProperty("role")
        private String role;
        
        @JsonProperty("content")
        private String content;
        
        public GrokMessage() {}
        
        // Getters and Setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class GrokUsage {
        
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
        
        public GrokUsage() {}
        
        // Getters and Setters
        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }
        
        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }
        
        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
    }
}