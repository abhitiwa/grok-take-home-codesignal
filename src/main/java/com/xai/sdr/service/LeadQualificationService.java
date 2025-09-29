package com.xai.sdr.service;

import com.xai.sdr.model.Lead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class LeadQualificationService {
    
    private final GrokApiService grokApiService;
    
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
            return LeadQualificationResult.builder()
                    .score(50) // Default neutral score
                    .reasoning("Unable to qualify lead due to technical error")
                    .build();
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
            return LeadQualificationResult.builder()
                    .score(50)
                    .reasoning("Unable to re-qualify lead due to technical error")
                    .build();
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
            
            return LeadQualificationResult.builder()
                    .score(score)
                    .reasoning(reasoning)
                    .recommendations(recommendations)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error parsing qualification response for lead {}: {}", lead.getId(), e.getMessage());
            return LeadQualificationResult.builder()
                    .score(50)
                    .reasoning("Error parsing AI response")
                    .recommendations("Manual review required")
                    .build();
        }
    }
    
    /**
     * Result class for lead qualification
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LeadQualificationResult {
        private int score;
        private String reasoning;
        private String recommendations;
    }
}