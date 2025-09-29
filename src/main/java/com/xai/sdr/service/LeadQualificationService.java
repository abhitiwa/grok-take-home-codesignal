package com.xai.sdr.service;

import com.xai.sdr.model.Lead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for lead qualification using Grok AI.
 * 
 * Analyzes lead data and provides qualification scores and reasoning
 * based on various criteria to help prioritize sales efforts.
 */
@Service
public class LeadQualificationService {
    
    private static final Logger log = LoggerFactory.getLogger(LeadQualificationService.class);
    
    private final GrokApiService grokApiService;
    
    @Autowired
    public LeadQualificationService(GrokApiService grokApiService) {
        this.grokApiService = grokApiService;
    }
    
    /**
     * Qualify a lead and assign a score based on available data
     */
    public LeadQualificationResult qualifyLead(Lead lead) {
        try {
            String prompt = buildQualificationPrompt(lead);
            String response = grokApiService.sendChatCompletion(prompt, 0.3); // Lower temperature for consistency
            
            return parseQualificationResponse(response, lead);
            
        } catch (Exception e) {
            log.error("Error qualifying lead {}: {}", lead.getId(), e.getMessage());
            return new LeadQualificationResult(
                50, // Default neutral score
                "Unable to qualify lead due to technical error",
                "Manual review required"
            );
        }
    }
    
    /**
     * Re-qualify a lead with custom criteria
     */
    public LeadQualificationResult requalifyLead(Lead lead, Map<String, Object> customCriteria) {
        try {
            String prompt = buildCustomQualificationPrompt(lead, customCriteria);
            String response = grokApiService.sendChatCompletion(prompt, 0.3);
            
            return parseQualificationResponse(response, lead);
            
        } catch (Exception e) {
            log.error("Error re-qualifying lead {}: {}", lead.getId(), e.getMessage());
            return new LeadQualificationResult(
                50,
                "Unable to re-qualify lead due to technical error",
                "Manual review required"
            );
        }
    }
    
    /**
     * Build the qualification prompt for Grok
     */
    private String buildQualificationPrompt(Lead lead) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert sales development representative analyzing a potential lead. ");
        prompt.append("Please evaluate the following lead and provide a qualification score from 0-100, ");
        prompt.append("where 100 is a perfect fit and 0 is not qualified at all.\n\n");
        
        prompt.append("Lead Information:\n");
        prompt.append("Name: ").append(lead.getFullName()).append("\n");
        prompt.append("Title: ").append(lead.getTitle() != null ? lead.getTitle() : "Not specified").append("\n");
        prompt.append("Company: ").append(lead.getCompanyName() != null ? lead.getCompanyName() : "Not specified").append("\n");
        prompt.append("Company Size: ").append(lead.getCompanySize() != null ? lead.getCompanySize() : "Not specified").append("\n");
        prompt.append("Industry: ").append(lead.getIndustry() != null ? lead.getIndustry() : "Not specified").append("\n");
        prompt.append("Location: ").append(lead.getLocation() != null ? lead.getLocation() : "Not specified").append("\n");
        prompt.append("Website: ").append(lead.getWebsite() != null ? lead.getWebsite() : "Not specified").append("\n");
        prompt.append("LinkedIn: ").append(lead.getLinkedinUrl() != null ? lead.getLinkedinUrl() : "Not specified").append("\n");
        
        if (lead.getNotes() != null && !lead.getNotes().trim().isEmpty()) {
            prompt.append("Additional Notes: ").append(lead.getNotes()).append("\n");
        }
        
        prompt.append("\nEvaluation Criteria:\n");
        prompt.append("1. Company size and growth potential (20 points)\n");
        prompt.append("2. Industry alignment with our target markets (20 points)\n");
        prompt.append("3. Decision-making authority based on title (20 points)\n");
        prompt.append("4. Contact information completeness (10 points)\n");
        prompt.append("5. Geographic location relevance (10 points)\n");
        prompt.append("6. Online presence and credibility (10 points)\n");
        prompt.append("7. Overall fit and potential (10 points)\n\n");
        
        prompt.append("Please respond in the following format:\n");
        prompt.append("SCORE: [number from 0-100]\n");
        prompt.append("REASONING: [detailed explanation of your scoring decision]\n");
        prompt.append("RECOMMENDATIONS: [specific next steps for this lead]\n");
        
        return prompt.toString();
    }
    
    /**
     * Build custom qualification prompt with user-defined criteria
     */
    private String buildCustomQualificationPrompt(Lead lead, Map<String, Object> customCriteria) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert sales development representative analyzing a potential lead. ");
        prompt.append("Please evaluate the following lead using the custom criteria provided and give a score from 0-100.\n\n");
        
        prompt.append("Lead Information:\n");
        prompt.append("Name: ").append(lead.getFullName()).append("\n");
        prompt.append("Title: ").append(lead.getTitle() != null ? lead.getTitle() : "Not specified").append("\n");
        prompt.append("Company: ").append(lead.getCompanyName() != null ? lead.getCompanyName() : "Not specified").append("\n");
        prompt.append("Company Size: ").append(lead.getCompanySize() != null ? lead.getCompanySize() : "Not specified").append("\n");
        prompt.append("Industry: ").append(lead.getIndustry() != null ? lead.getIndustry() : "Not specified").append("\n");
        prompt.append("Location: ").append(lead.getLocation() != null ? lead.getLocation() : "Not specified").append("\n");
        
        prompt.append("\nCustom Evaluation Criteria:\n");
        for (Map.Entry<String, Object> entry : customCriteria.entrySet()) {
            prompt.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        prompt.append("\nPlease respond in the following format:\n");
        prompt.append("SCORE: [number from 0-100]\n");
        prompt.append("REASONING: [detailed explanation based on custom criteria]\n");
        prompt.append("RECOMMENDATIONS: [specific next steps for this lead]\n");
        
        return prompt.toString();
    }
    
    /**
     * Parse the qualification response from Grok
     */
    private LeadQualificationResult parseQualificationResponse(String response, Lead lead) {
        try {
            int score = 50; // Default score
            String reasoning = "Unable to parse response";
            String recommendations = "Contact lead for more information";
            
            if (response != null) {
                String[] lines = response.split("\n");
                
                for (String line : lines) {
                    if (line.startsWith("SCORE:")) {
                        try {
                            String scoreStr = line.substring(6).trim();
                            score = Integer.parseInt(scoreStr);
                            score = Math.max(0, Math.min(100, score)); // Clamp between 0-100
                        } catch (NumberFormatException e) {
                            log.warn("Could not parse score from response: {}", line);
                        }
                    } else if (line.startsWith("REASONING:")) {
                        reasoning = line.substring(10).trim();
                    } else if (line.startsWith("RECOMMENDATIONS:")) {
                        recommendations = line.substring(16).trim();
                    }
                }
            }
            
            return new LeadQualificationResult(score, reasoning, recommendations);
                    
        } catch (Exception e) {
            log.error("Error parsing qualification response for lead {}: {}", lead.getId(), e.getMessage());
            return new LeadQualificationResult(
                50,
                "Error parsing AI response",
                "Manual review required"
            );
        }
    }
    
    /**
     * Result class for lead qualification
     */
    public static class LeadQualificationResult {
        private int score;
        private String reasoning;
        private String recommendations;
        
        public LeadQualificationResult() {}
        
        public LeadQualificationResult(int score, String reasoning, String recommendations) {
            this.score = score;
            this.reasoning = reasoning;
            this.recommendations = recommendations;
        }
        
        // Getters and Setters
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        
        public String getReasoning() { return reasoning; }
        public void setReasoning(String reasoning) { this.reasoning = reasoning; }
        
        public String getRecommendations() { return recommendations; }
        public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    }
}