package com.xai.sdr.controller;

import com.xai.sdr.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for model evaluation operations.
 * 
 * Provides endpoints for testing and evaluating Grok's performance
 * across different sales scenarios and prompt variations.
 */
@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EvaluationController {
    
    private final EvaluationService evaluationService;
    
    /**
     * Run evaluation tests for lead qualification
     */
    @PostMapping("/qualification")
    public ResponseEntity<Map<String, Object>> evaluateQualification(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> results = evaluationService.evaluateQualification(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error running qualification evaluation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Run evaluation tests for messaging generation
     */
    @PostMapping("/messaging")
    public ResponseEntity<Map<String, Object>> evaluateMessaging(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> results = evaluationService.evaluateMessaging(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error running messaging evaluation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Test different prompt variations
     */
    @PostMapping("/prompts")
    public ResponseEntity<Map<String, Object>> evaluatePrompts(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> results = evaluationService.evaluatePromptVariations(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error running prompt evaluation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get evaluation history
     */
    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getEvaluationHistory() {
        try {
            List<Map<String, Object>> history = evaluationService.getEvaluationHistory();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error retrieving evaluation history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get evaluation metrics and statistics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getEvaluationMetrics() {
        try {
            Map<String, Object> metrics = evaluationService.getEvaluationMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error retrieving evaluation metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Run comprehensive evaluation suite
     */
    @PostMapping("/comprehensive")
    public ResponseEntity<Map<String, Object>> runComprehensiveEvaluation() {
        try {
            Map<String, Object> results = evaluationService.runComprehensiveEvaluation();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error running comprehensive evaluation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Test API connection and basic functionality
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> testApiHealth() {
        try {
            Map<String, Object> health = evaluationService.testApiHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Error testing API health", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}