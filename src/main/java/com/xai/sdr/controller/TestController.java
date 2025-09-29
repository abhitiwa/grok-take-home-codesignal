package com.xai.sdr.controller;

import com.xai.sdr.model.Lead;
import com.xai.sdr.service.GrokApiService;
import com.xai.sdr.service.LeadQualificationService;
import com.xai.sdr.service.PersonalizedMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private GrokApiService grokApiService;
    
    @Autowired
    private LeadQualificationService leadQualificationService;
    
    @Autowired
    private PersonalizedMessagingService personalizedMessagingService;
    
    @GetMapping("/grok-connection")
    public String testGrokConnection() {
        try {
            boolean connected = grokApiService.testConnection();
            return connected ? "Grok API: Connected ✅" : "Grok API: Connection Failed ❌";
        } catch (Exception e) {
            return "Grok API Error: " + e.getMessage();
        }
    }
    
    @PostMapping("/grok-message")
    public String testGrokMessage(@RequestBody String message) {
        try {
            return "Grok Response: " + grokApiService.sendChatCompletion(message);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    @PostMapping("/test-qualification")
    public Object testLeadQualification() {
        // Create a test lead
        Lead testLead = new Lead();
        testLead.setId(1L);
        testLead.setFirstName("John");
        testLead.setLastName("Doe");
        testLead.setEmail("john.doe@techcorp.com");
        testLead.setTitle("VP of Engineering");
        testLead.setCompanyName("TechCorp Inc");
        testLead.setCompanySize("1000+");
        testLead.setIndustry("Technology");
        testLead.setLocation("San Francisco, CA");
        testLead.setWebsite("https://techcorp.com");
        
        LeadQualificationService.LeadQualificationResult result = 
            leadQualificationService.qualifyLead(testLead);
        
        return result;
    }
    
    @PostMapping("/test-messaging")
    public String testMessageGeneration() {
        // Create a test lead
        Lead testLead = new Lead();
        testLead.setId(2L);
        testLead.setFirstName("Jane");
        testLead.setLastName("Smith");
        testLead.setCompanyName("StartupCorp");
        testLead.setTitle("CTO");
        testLead.setIndustry("SaaS");
        testLead.setLocation("Austin, TX");
        
        return personalizedMessagingService.generateEmailMessage(testLead, "initial outreach");
    }
    
    @PostMapping("/test-linkedin-message")
    public String testLinkedInMessage() {
        Lead testLead = new Lead();
        testLead.setId(3L);
        testLead.setFirstName("Mike");
        testLead.setLastName("Johnson");
        testLead.setCompanyName("Growth Corp");
        testLead.setTitle("CEO");
        testLead.setIndustry("Marketing");
        
        return personalizedMessagingService.generateLinkedInMessage(testLead, "connection request");
    }
    
    @PostMapping("/test-follow-up")
    public String testFollowUpMessage() {
        Lead testLead = new Lead();
        testLead.setId(4L);
        testLead.setFirstName("Sarah");
        testLead.setLastName("Wilson");
        testLead.setCompanyName("Enterprise Solutions");
        testLead.setTitle("Director of Operations");
        
        return personalizedMessagingService.generateFollowUpMessage(
            testLead, 
            "Had a great initial call about scaling challenges", 
            "technical follow-up"
        );
    }
}