package com.xai.sdr.service;

import com.xai.sdr.model.Lead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for evaluating Grok's performance across different sales scenarios.
 * 
 * Provides systematic testing and evaluation of AI responses to identify
 * areas for improvement and optimize prompt engineering.
 */
@Service
public class EvaluationService {
    
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    
    private final GrokApiService grokApiService;
    private final LeadQualificationService qualificationService;
    private final PersonalizedMessagingService messagingService;
    
    private final List<Map<String, Object>> evaluationHistory = new ArrayList<>();
    
    @Autowired
    public EvaluationService(GrokApiService grokApiService,
                           LeadQualificationService qualificationService,
                           PersonalizedMessagingService messagingService) {
        this.grokApiService = grokApiService;
        this.qualificationService = qualificationService;
        this.messagingService = messagingService;
    }
    
    /**
     * Evaluate lead qualification performance
     */
    public Map<String, Object> evaluateQualification(Map<String, Object> request) {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        // Sample test leads for evaluation
        List<Lead> testLeads = createTestLeads();
        
        for (Lead lead : testLeads) {
            try {
                long startTime = System.currentTimeMillis();
                LeadQualificationService.LeadQualificationResult qualificationResult = qualificationService.qualifyLead(lead);
                long endTime = System.currentTimeMillis();
                
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("leadId", lead.getId());
                testResult.put("leadName", lead.getFullName());
                testResult.put("company", lead.getCompanyName());
                testResult.put("score", qualificationResult.getScore());
                testResult.put("reasoning", qualificationResult.getReasoning());
                testResult.put("responseTime", endTime - startTime);
                testResult.put("success", true);
                
                testResults.add(testResult);
                
            } catch (Exception e) {
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("leadId", lead.getId());
                testResult.put("leadName", lead.getFullName());
                testResult.put("error", e.getMessage());
                testResult.put("success", false);
                testResults.add(testResult);
            }
        }
        
        results.put("testResults", testResults);
        results.put("totalTests", testLeads.size());
        results.put("successfulTests", testResults.stream().mapToInt(r -> (Boolean) r.get("success") ? 1 : 0).sum());
        results.put("averageScore", calculateAverageScore(testResults));
        results.put("averageResponseTime", calculateAverageResponseTime(testResults));
        
        // Store in history
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("type", "qualification");
        historyEntry.put("timestamp", LocalDateTime.now());
        historyEntry.put("results", results);
        evaluationHistory.add(historyEntry);
        
        return results;
    }
    
    /**
     * Evaluate messaging generation performance
     */
    public Map<String, Object> evaluateMessaging(Map<String, Object> request) {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        List<Lead> testLeads = createTestLeads();
        String[] messageTypes = {"initial outreach", "follow-up", "meeting request", "proposal follow-up"};
        
        for (Lead lead : testLeads) {
            for (String messageType : messageTypes) {
                try {
                    long startTime = System.currentTimeMillis();
                    String emailMessage = messagingService.generateEmailMessage(lead, messageType);
                    long endTime = System.currentTimeMillis();
                    
                    Map<String, Object> testResult = new HashMap<>();
                    testResult.put("leadId", lead.getId());
                    testResult.put("messageType", messageType);
                    testResult.put("message", emailMessage);
                    testResult.put("messageLength", emailMessage.length());
                    testResult.put("responseTime", endTime - startTime);
                    testResult.put("success", true);
                    
                    testResults.add(testResult);
                    
                } catch (Exception e) {
                    Map<String, Object> testResult = new HashMap<>();
                    testResult.put("leadId", lead.getId());
                    testResult.put("messageType", messageType);
                    testResult.put("error", e.getMessage());
                    testResult.put("success", false);
                    testResults.add(testResult);
                }
            }
        }
        
        results.put("testResults", testResults);
        results.put("totalTests", testLeads.size() * messageTypes.length);
        results.put("successfulTests", testResults.stream().mapToInt(r -> (Boolean) r.get("success") ? 1 : 0).sum());
        results.put("averageMessageLength", calculateAverageMessageLength(testResults));
        results.put("averageResponseTime", calculateAverageResponseTime(testResults));
        
        // Store in history
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("type", "messaging");
        historyEntry.put("timestamp", LocalDateTime.now());
        historyEntry.put("results", results);
        evaluationHistory.add(historyEntry);
        
        return results;
    }
    
    /**
     * Evaluate different prompt variations
     */
    public Map<String, Object> evaluatePromptVariations(Map<String, Object> request) {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        Lead testLead = createTestLeads().get(0);
        String[] promptVariations = {
            "You are an expert sales development representative...",
            "As a senior sales professional with 10+ years experience...",
            "You are a data-driven sales analyst evaluating leads...",
            "You are a consultative sales expert focused on value creation..."
        };
        
        for (int i = 0; i < promptVariations.length; i++) {
            try {
                long startTime = System.currentTimeMillis();
                String customPrompt = promptVariations[i] + " Please evaluate this lead: " + 
                    testLead.getFullName() + " at " + testLead.getCompanyName();
                String response = grokApiService.sendChatCompletion(customPrompt);
                long endTime = System.currentTimeMillis();
                
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("promptVariation", i + 1);
                testResult.put("prompt", promptVariations[i]);
                testResult.put("response", response);
                testResult.put("responseLength", response.length());
                testResult.put("responseTime", endTime - startTime);
                testResult.put("success", true);
                
                testResults.add(testResult);
                
            } catch (Exception e) {
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("promptVariation", i + 1);
                testResult.put("error", e.getMessage());
                testResult.put("success", false);
                testResults.add(testResult);
            }
        }
        
        results.put("testResults", testResults);
        results.put("totalTests", promptVariations.length);
        results.put("successfulTests", testResults.stream().mapToInt(r -> (Boolean) r.get("success") ? 1 : 0).sum());
        results.put("averageResponseLength", calculateAverageResponseLength(testResults));
        results.put("averageResponseTime", calculateAverageResponseTime(testResults));
        
        // Store in history
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("type", "prompt_variations");
        historyEntry.put("timestamp", LocalDateTime.now());
        historyEntry.put("results", results);
        evaluationHistory.add(historyEntry);
        
        return results;
    }
    
    /**
     * Run comprehensive evaluation suite
     */
    public Map<String, Object> runComprehensiveEvaluation() {
        Map<String, Object> comprehensiveResults = new HashMap<>();
        
        // Test API health
        Map<String, Object> healthResults = testApiHealth();
        comprehensiveResults.put("health", healthResults);
        
        // Test qualification
        Map<String, Object> qualificationResults = evaluateQualification(new HashMap<>());
        comprehensiveResults.put("qualification", qualificationResults);
        
        // Test messaging
        Map<String, Object> messagingResults = evaluateMessaging(new HashMap<>());
        comprehensiveResults.put("messaging", messagingResults);
        
        // Test prompt variations
        Map<String, Object> promptResults = evaluatePromptVariations(new HashMap<>());
        comprehensiveResults.put("promptVariations", promptResults);
        
        // Overall metrics
        Map<String, Object> overallMetrics = new HashMap<>();
        overallMetrics.put("totalEvaluations", evaluationHistory.size());
        overallMetrics.put("lastEvaluation", LocalDateTime.now());
        overallMetrics.put("systemHealth", "Good");
        
        comprehensiveResults.put("overall", overallMetrics);
        
        return comprehensiveResults;
    }
    
    /**
     * Test API health and basic functionality
     */
    public Map<String, Object> testApiHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            boolean connectionTest = grokApiService.testConnection();
            health.put("apiConnection", connectionTest);
            health.put("status", connectionTest ? "Healthy" : "Unhealthy");
            
            if (connectionTest) {
                long startTime = System.currentTimeMillis();
                String testResponse = grokApiService.sendChatCompletion("Hello, this is a health check.");
                long endTime = System.currentTimeMillis();
                
                health.put("responseTime", endTime - startTime);
                health.put("responseReceived", testResponse != null && !testResponse.isEmpty());
            }
            
        } catch (Exception e) {
            health.put("apiConnection", false);
            health.put("status", "Error");
            health.put("error", e.getMessage());
        }
        
        return health;
    }
    
    /**
     * Get evaluation history
     */
    public List<Map<String, Object>> getEvaluationHistory() {
        return new ArrayList<>(evaluationHistory);
    }
    
    /**
     * Get evaluation metrics and statistics
     */
    public Map<String, Object> getEvaluationMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("totalEvaluations", evaluationHistory.size());
        metrics.put("lastEvaluation", evaluationHistory.isEmpty() ? null : 
            evaluationHistory.get(evaluationHistory.size() - 1).get("timestamp"));
        
        // Count by type
        Map<String, Long> evaluationsByType = new HashMap<>();
        for (Map<String, Object> entry : evaluationHistory) {
            String type = (String) entry.get("type");
            evaluationsByType.put(type, evaluationsByType.getOrDefault(type, 0L) + 1);
        }
        metrics.put("evaluationsByType", evaluationsByType);
        
        return metrics;
    }
    
    /**
     * Create test leads for evaluation
     */
    private List<Lead> createTestLeads() {
        List<Lead> testLeads = new ArrayList<>();
        
        // High-value enterprise lead
        Lead enterpriseLead = new Lead();
        enterpriseLead.setId(1L);
        enterpriseLead.setFirstName("John");
        enterpriseLead.setLastName("Smith");
        enterpriseLead.setEmail("john.smith@techcorp.com");
        enterpriseLead.setTitle("VP of Engineering");
        enterpriseLead.setCompanyName("TechCorp Inc");
        enterpriseLead.setCompanySize("1000+");
        enterpriseLead.setIndustry("Technology");
        enterpriseLead.setLocation("San Francisco, CA");
        enterpriseLead.setWebsite("https://techcorp.com");
        testLeads.add(enterpriseLead);
        
        // Mid-market lead
        Lead midMarketLead = new Lead();
        midMarketLead.setId(2L);
        midMarketLead.setFirstName("Sarah");
        midMarketLead.setLastName("Johnson");
        midMarketLead.setEmail("sarah.johnson@startup.io");
        midMarketLead.setTitle("CTO");
        midMarketLead.setCompanyName("StartupIO");
        midMarketLead.setCompanySize("50-100");
        midMarketLead.setIndustry("SaaS");
        midMarketLead.setLocation("Austin, TX");
        midMarketLead.setWebsite("https://startup.io");
        testLeads.add(midMarketLead);
        
        // Small business lead
        Lead smallBusinessLead = new Lead();
        smallBusinessLead.setId(3L);
        smallBusinessLead.setFirstName("Mike");
        smallBusinessLead.setLastName("Davis");
        smallBusinessLead.setEmail("mike@localbiz.com");
        smallBusinessLead.setTitle("Owner");
        smallBusinessLead.setCompanyName("Local Business Solutions");
        smallBusinessLead.setCompanySize("10-50");
        smallBusinessLead.setIndustry("Professional Services");
        smallBusinessLead.setLocation("Denver, CO");
        testLeads.add(smallBusinessLead);
        
        return testLeads;
    }
    
    /**
     * Calculate average score from test results
     */
    private double calculateAverageScore(List<Map<String, Object>> testResults) {
        return testResults.stream()
                .filter(r -> r.containsKey("score"))
                .mapToInt(r -> (Integer) r.get("score"))
                .average()
                .orElse(0.0);
    }
    
    /**
     * Calculate average response time from test results
     */
    private double calculateAverageResponseTime(List<Map<String, Object>> testResults) {
        return testResults.stream()
                .filter(r -> r.containsKey("responseTime"))
                .mapToLong(r -> (Long) r.get("responseTime"))
                .average()
                .orElse(0.0);
    }
    
    /**
     * Calculate average message length from test results
     */
    private double calculateAverageMessageLength(List<Map<String, Object>> testResults) {
        return testResults.stream()
                .filter(r -> r.containsKey("messageLength"))
                .mapToInt(r -> (Integer) r.get("messageLength"))
                .average()
                .orElse(0.0);
    }
    
    /**
     * Calculate average response length from test results
     */
    private double calculateAverageResponseLength(List<Map<String, Object>> testResults) {
        return testResults.stream()
                .filter(r -> r.containsKey("responseLength"))
                .mapToInt(r -> (Integer) r.get("responseLength"))
                .average()
                .orElse(0.0);
    }
}