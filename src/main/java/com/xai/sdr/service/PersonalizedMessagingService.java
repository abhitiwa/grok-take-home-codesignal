package com.xai.sdr.service;

import com.xai.sdr.model.Lead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating personalized messages using Grok AI.
 * 
 * Creates customized outreach messages based on lead data and
 * communication preferences to improve engagement rates.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalizedMessagingService {
    
    private final GrokApiService grokApiService;
    
    /**
     * Generate a personalized email message for a lead
     */
    public String generateEmailMessage(Lead lead, String messageType) {
        try {
            String prompt = buildEmailPrompt(lead, messageType);
            String response = grokApiService.sendChatCompletion(prompt, 0.7); // Higher temperature for creativity
            
            return cleanAndFormatMessage(response);
            
        } catch (Exception e) {
            log.error("Error generating email message for lead {}: {}", lead.getId(), e.getMessage());
            return generateFallbackMessage(lead, messageType);
        }
    }
    
    /**
     * Generate a personalized LinkedIn message for a lead
     */
    public String generateLinkedInMessage(Lead lead, String messageType) {
        try {
            String prompt = buildLinkedInPrompt(lead, messageType);
            String response = grokApiService.sendChatCompletion(prompt, 0.7);
            
            return cleanAndFormatMessage(response);
            
        } catch (Exception e) {
            log.error("Error generating LinkedIn message for lead {}: {}", lead.getId(), e.getMessage());
            return generateFallbackLinkedInMessage(lead, messageType);
        }
    }
    
    /**
     * Generate a follow-up message based on previous interaction
     */
    public String generateFollowUpMessage(Lead lead, String previousActivity, String messageType) {
        try {
            String prompt = buildFollowUpPrompt(lead, previousActivity, messageType);
            String response = grokApiService.sendChatCompletion(prompt, 0.6);
            
            return cleanAndFormatMessage(response);
            
        } catch (Exception e) {
            log.error("Error generating follow-up message for lead {}: {}", lead.getId(), e.getMessage());
            return generateFallbackFollowUpMessage(lead, messageType);
        }
    }
    
    /**
     * Generate a meeting request message
     */
    public String generateMeetingRequest(Lead lead, String meetingType, String proposedTime) {
        try {
            String prompt = buildMeetingRequestPrompt(lead, meetingType, proposedTime);
            String response = grokApiService.sendChatCompletion(prompt, 0.5);
            
            return cleanAndFormatMessage(response);
            
        } catch (Exception e) {
            log.error("Error generating meeting request for lead {}: {}", lead.getId(), e.getMessage());
            return generateFallbackMeetingRequest(lead, meetingType, proposedTime);
        }
    }
    
    /**
     * Build email prompt for Grok
     */
    private String buildEmailPrompt(Lead lead, String messageType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert sales development representative writing a personalized email. ");
        prompt.append("Create a professional, engaging email that feels personal and relevant to the recipient.\n\n");
        
        prompt.append("Lead Information:\n");
        prompt.append("Name: ").append(lead.getFullName()).append("\n");
        prompt.append("Title: ").append(lead.getTitle() != null ? lead.getTitle() : "Not specified").append("\n");
        prompt.append("Company: ").append(lead.getCompanyName() != null ? lead.getCompanyName() : "Not specified").append("\n");
        prompt.append("Industry: ").append(lead.getIndustry() != null ? lead.getIndustry() : "Not specified").append("\n");
        prompt.append("Location: ").append(lead.getLocation() != null ? lead.getLocation() : "Not specified").append("\n");
        
        if (lead.getCompanySize() != null) {
            prompt.append("Company Size: ").append(lead.getCompanySize()).append("\n");
        }
        
        prompt.append("\nMessage Type: ").append(messageType).append("\n\n");
        
        prompt.append("Guidelines:\n");
        prompt.append("- Keep it concise (under 150 words)\n");
        prompt.append("- Use a professional but friendly tone\n");
        prompt.append("- Include a clear value proposition\n");
        prompt.append("- End with a specific call-to-action\n");
        prompt.append("- Personalize based on their role and company\n");
        prompt.append("- Avoid generic sales language\n\n");
        
        prompt.append("Write only the email body content, no subject line or signatures needed.");
        
        return prompt.toString();
    }
    
    /**
     * Build LinkedIn prompt for Grok
     */
    private String buildLinkedInPrompt(Lead lead, String messageType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are writing a personalized LinkedIn message for sales outreach. ");
        prompt.append("LinkedIn messages should be shorter and more casual than emails.\n\n");
        
        prompt.append("Lead Information:\n");
        prompt.append("Name: ").append(lead.getFullName()).append("\n");
        prompt.append("Title: ").append(lead.getTitle() != null ? lead.getTitle() : "Not specified").append("\n");
        prompt.append("Company: ").append(lead.getCompanyName() != null ? lead.getCompanyName() : "Not specified").append("\n");
        prompt.append("Industry: ").append(lead.getIndustry() != null ? lead.getIndustry() : "Not specified").append("\n");
        
        prompt.append("\nMessage Type: ").append(messageType).append("\n\n");
        
        prompt.append("Guidelines:\n");
        prompt.append("- Keep it under 100 words\n");
        prompt.append("- Use a conversational tone\n");
        prompt.append("- Reference something specific about their profile or company\n");
        prompt.append("- Include a soft call-to-action\n");
        prompt.append("- Avoid being too salesy\n\n");
        
        prompt.append("Write only the message content.");
        
        return prompt.toString();
    }
    
    /**
     * Build follow-up prompt for Grok
     */
    private String buildFollowUpPrompt(Lead lead, String previousActivity, String messageType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are writing a follow-up message based on a previous interaction. ");
        prompt.append("Make it relevant to what was discussed before.\n\n");
        
        prompt.append("Lead Information:\n");
        prompt.append("Name: ").append(lead.getFullName()).append("\n");
        prompt.append("Title: ").append(lead.getTitle() != null ? lead.getTitle() : "Not specified").append("\n");
        prompt.append("Company: ").append(lead.getCompanyName() != null ? lead.getCompanyName() : "Not specified").append("\n");
        
        prompt.append("\nPrevious Activity: ").append(previousActivity).append("\n");
        prompt.append("Follow-up Type: ").append(messageType).append("\n\n");
        
        prompt.append("Guidelines:\n");
        prompt.append("- Reference the previous interaction\n");
        prompt.append("- Provide additional value or information\n");
        prompt.append("- Keep it relevant and timely\n");
        prompt.append("- Include a clear next step\n\n");
        
        prompt.append("Write the follow-up message content.");
        
        return prompt.toString();
    }
    
    /**
     * Build meeting request prompt for Grok
     */
    private String buildMeetingRequestPrompt(Lead lead, String meetingType, String proposedTime) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are requesting a meeting with a potential client. ");
        prompt.append("Make it professional and provide clear value for the meeting.\n\n");
        
        prompt.append("Lead Information:\n");
        prompt.append("Name: ").append(lead.getFullName()).append("\n");
        prompt.append("Title: ").append(lead.getTitle() != null ? lead.getTitle() : "Not specified").append("\n");
        prompt.append("Company: ").append(lead.getCompanyName() != null ? lead.getCompanyName() : "Not specified").append("\n");
        
        prompt.append("\nMeeting Type: ").append(meetingType).append("\n");
        prompt.append("Proposed Time: ").append(proposedTime).append("\n\n");
        
        prompt.append("Guidelines:\n");
        prompt.append("- Explain the value of the meeting\n");
        prompt.append("- Be specific about what will be discussed\n");
        prompt.append("- Offer flexibility in scheduling\n");
        prompt.append("- Keep it professional and respectful\n\n");
        
        prompt.append("Write the meeting request message.");
        
        return prompt.toString();
    }
    
    /**
     * Clean and format the AI-generated message
     */
    private String cleanAndFormatMessage(String message) {
        if (message == null) {
            return "Unable to generate message at this time.";
        }
        
        // Remove any unwanted prefixes or formatting
        String cleaned = message.trim();
        
        // Remove common AI response prefixes
        if (cleaned.startsWith("Here's") || cleaned.startsWith("Here is")) {
            cleaned = cleaned.substring(cleaned.indexOf(":") + 1).trim();
        }
        
        return cleaned;
    }
    
    /**
     * Generate fallback email message
     */
    private String generateFallbackMessage(Lead lead, String messageType) {
        return "Hi " + lead.getFirstName() + ",\n\n" +
               "I hope this message finds you well. I wanted to reach out regarding " + messageType + ".\n\n" +
               "I'd love to learn more about your current challenges and see how we might be able to help.\n\n" +
               "Would you be available for a brief conversation this week?\n\n" +
               "Best regards,\n" +
               "Sales Team";
    }
    
    /**
     * Generate fallback LinkedIn message
     */
    private String generateFallbackLinkedInMessage(Lead lead, String messageType) {
        return "Hi " + lead.getFirstName() + ", I noticed your role at " + 
               (lead.getCompanyName() != null ? lead.getCompanyName() : "your company") + 
               ". I'd love to connect and share some insights about " + messageType + ". " +
               "Would you be interested in a brief conversation?";
    }
    
    /**
     * Generate fallback follow-up message
     */
    private String generateFallbackFollowUpMessage(Lead lead, String messageType) {
        return "Hi " + lead.getFirstName() + ",\n\n" +
               "Following up on our previous conversation about " + messageType + ".\n\n" +
               "I wanted to share some additional information that might be relevant to your situation.\n\n" +
               "Would you like to schedule a follow-up call to discuss further?\n\n" +
               "Best regards,\n" +
               "Sales Team";
    }
    
    /**
     * Generate fallback meeting request
     */
    private String generateFallbackMeetingRequest(Lead lead, String meetingType, String proposedTime) {
        return "Hi " + lead.getFirstName() + ",\n\n" +
               "I'd like to schedule a " + meetingType + " to discuss how we can help " + 
               (lead.getCompanyName() != null ? lead.getCompanyName() : "your company") + 
               " achieve your goals.\n\n" +
               "I'm available " + proposedTime + ". Would this work for you?\n\n" +
               "Best regards,\n" +
               "Sales Team";
    }
}