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
 * Optimized for demo purposes with reduced test sets for faster execution.
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
     * Evaluate lead qualification performance (demo optimized - single lead)
     */
    public Map<String, Object> evaluateQualification(Map<String, Object> request) {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        // Single test lead for faster demo
        Lead testLead = createDemoLead();
        
        try {
            long startTime = System.currentTimeMillis();
            LeadQualificationService.LeadQualificationResult qualificationResult = qualificationService.qualifyLead(testLead);
            long endTime = System.currentTimeMillis();
            
            Map<String, Object> testResult = new HashMap<>();
            testResult.put("leadId", testLead.getId());
            testResult.put("leadName", testLead.getFullName());
            testResult.put("company", testLead.getCompanyName());
            testResult.put("score", qualificationResult.getScore());
            testResult.put("reasoning", qualificationResult.getReasoning());
            testResult.put("responseTime", endTime - startTime);
            testResult.put("success", true);
            
            testResults.add(testResult);
            
        } catch (Exception e) {
            Map<String, Object> testResult = new HashMap<>();
            testResult.put("leadId", testLead.getId());
            testResult.put("leadName", testLead.getFullName());
            testResult.put("error", e.getMessage());
            testResult.put("success", false);
            testResults.add(testResult);
        }
        
        results.put("testResults", testResults);
        results.put("totalTests", 1);
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
     * Evaluate messaging generation performance (demo optimized - single lead, single message type)
     */
    public Map<String, Object> evaluateMessaging(Map<String, Object> request) {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        Lead testLead = createDemoLead();
        String messageType = "initial outreach"; // Single message type for demo
        
        try {
            long startTime = System.currentTimeMillis();
            String emailMessage = messagingService.generateEmailMessage(testLead, messageType);
            long endTime = System.currentTimeMillis();
            
            Map<String, Object> testResult = new HashMap<>();
            testResult.put("leadId", testLead.getId());
            testResult.put("messageType", messageType);
            testResult.put("message", emailMessage);
            testResult.put("messageLength", emailMessage.length());
            testResult.put("responseTime", endTime - startTime);
            testResult.put("success", true);
            
            testResults.add(testResult);
            
        } catch (Exception e) {
            Map<String, Object> testResult = new HashMap<>();
            testResult.put("leadId", testLead.getId());
            testResult.put("messageType", messageType);
            testResult.put("error", e.getMessage());
            testResult.put("success", false);
            testResults.add(testResult);
        }
        
        results.put("testResults", testResults);
        results.put("totalTests", 1);
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
     * Evaluate different prompt variations (demo optimized - 2 variations only)
     */
    public Map<String, Object> evaluatePromptVariations(Map<String, Object> request) {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();
        
        Lead testLead = createDemoLead();
        // Reduced to 2 prompt variations for faster demo
        String[] promptVariations = {
            "You are an expert sales development representative...",
            "As a senior sales professional with 10+ years experience..."
        };
        
        for (int i = 0; i < promptVariations.length; i++) {
            try {
                long startTime = System.currentTimeMillis();
                String customPrompt = promptVariations[i] + " Please evaluate this lead in one sentence: " + 
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
     * Run comprehensive evaluation suite (demo optimized with proper result aggregation)
     */
    public Map<String, Object> runComprehensiveEvaluation() {
        Map<String, Object> comprehensiveResults = new HashMap<>();
        
        try {
            // Test API health (fast)
            Map<String, Object> healthResults = testApiHealth();
            comprehensiveResults.put("health", healthResults);
            
            // Run qualification test
            Map<String, Object> qualificationResults = evaluateQualification(new HashMap<>());
            comprehensiveResults.put("qualification", qualificationResults);
            
            // Run messaging test
            Map<String, Object> messagingResults = evaluateMessaging(new HashMap<>());
            comprehensiveResults.put("messaging", messagingResults);
            
            // Calculate combined metrics
            int totalTests = 0;
            int successfulTests = 0;
            double totalResponseTime = 0;
            int responseTimeCount = 0;
            
            // Add qualification metrics
            if (qualificationResults.containsKey("totalTests")) {
                totalTests += (Integer) qualificationResults.get("totalTests");
                successfulTests += (Integer) qualificationResults.get("successfulTests");
            }
            
            // Add messaging metrics  
            if (messagingResults.containsKey("totalTests")) {
                totalTests += (Integer) messagingResults.get("totalTests");
                successfulTests += (Integer) messagingResults.get("successfulTests");
            }
            
            // Calculate average response time
            if (qualificationResults.containsKey("averageResponseTime")) {
                totalResponseTime += (Double) qualificationResults.get("averageResponseTime");
                responseTimeCount++;
            }
            if (messagingResults.containsKey("averageResponseTime")) {
                totalResponseTime += (Double) messagingResults.get("averageResponseTime");
                responseTimeCount++;
            }
            
            // Set top-level metrics for the UI
            comprehensiveResults.put("totalTests", totalTests);
            comprehensiveResults.put("successfulTests", successfulTests);
            comprehensiveResults.put("averageResponseTime", responseTimeCount > 0 ? totalResponseTime / responseTimeCount : 0);
            
            // Overall metrics
            Map<String, Object> overallMetrics = new HashMap<>();
            overallMetrics.put("totalEvaluations", evaluationHistory.size());
            overallMetrics.put("lastEvaluation", LocalDateTime.now());
            overallMetrics.put("systemHealth", healthResults.get("status"));
            overallMetrics.put("qualificationScore", qualificationResults.get("averageScore"));
            overallMetrics.put("messagingSuccess", messagingResults.get("successfulTests"));
            
            comprehensiveResults.put("overall", overallMetrics);
            
            // Store in history
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("type", "comprehensive");
            historyEntry.put("timestamp", LocalDateTime.now());
            historyEntry.put("results", comprehensiveResults);
            evaluationHistory.add(historyEntry);
            
            log.info("Comprehensive evaluation completed: {} total tests, {} successful", totalTests, successfulTests);
            
        } catch (Exception e) {
            log.error("Error running comprehensive evaluation", e);
            
            // Return error state but with some data
            comprehensiveResults.put("totalTests", 0);
            comprehensiveResults.put("successfulTests", 0);
            comprehensiveResults.put("averageResponseTime", 0);
            comprehensiveResults.put("error", "Comprehensive evaluation failed: " + e.getMessage());
        }
        
        return comprehensiveResults;
    }
    
    /**
     * Test API health and basic functionality (fast test)
     */
    public Map<String, Object> testApiHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            boolean connectionTest = grokApiService.testConnection();
            health.put("apiConnection", connectionTest);
            health.put("status", connectionTest ? "Healthy" : "Unhealthy");
            
            if (connectionTest) {
                long startTime = System.currentTimeMillis();
                String testResponse = grokApiService.sendChatCompletion("Hello, respond with 'OK'.");
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
     * Create single demo lead for faster testing
     */
    private Lead createDemoLead() {
        Lead demoLead = new Lead();
        demoLead.setId(99L);
        demoLead.setFirstName("Demo");
        demoLead.setLastName("Lead");
        demoLead.setEmail("demo@enterprise.com");
        demoLead.setTitle("VP of Sales");
        demoLead.setCompanyName("Enterprise Corp");
        demoLead.setCompanySize("500-1000");
        demoLead.setIndustry("Technology");
        demoLead.setLocation("New York, NY");
        demoLead.setWebsite("https://enterprise.com");
        
        return demoLead;
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