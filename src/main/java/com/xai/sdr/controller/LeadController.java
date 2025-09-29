package com.xai.sdr.controller;

import com.xai.sdr.model.Lead;
import com.xai.sdr.model.PipelineStage;
import com.xai.sdr.service.LeadQualificationService;
import com.xai.sdr.service.LeadService;
import com.xai.sdr.service.PersonalizedMessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for lead management operations.
 * 
 * Provides endpoints for CRUD operations, qualification, messaging,
 * and pipeline management for sales leads.
 */
@RestController
@RequestMapping("/leads")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LeadController {
    
    private final LeadService leadService;
    private final LeadQualificationService qualificationService;
    private final PersonalizedMessagingService messagingService;
    
    /**
     * Get all leads with optional filtering
     */
    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) PipelineStage pipelineStage) {
        
        try {
            List<Lead> leads = leadService.searchLeads(firstName, lastName, companyName, industry, pipelineStage);
            return ResponseEntity.ok(leads);
        } catch (Exception e) {
            log.error("Error retrieving leads", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get a specific lead by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
        try {
            Optional<Lead> lead = leadService.findById(id);
            return lead.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error retrieving lead with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create a new lead
     */
    @PostMapping
    public ResponseEntity<Lead> createLead(@Valid @RequestBody Lead lead) {
        try {
            Lead savedLead = leadService.save(lead);
            log.info("Created new lead with id: {}", savedLead.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLead);
        } catch (Exception e) {
            log.error("Error creating lead", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update an existing lead
     */
    @PutMapping("/{id}")
    public ResponseEntity<Lead> updateLead(@PathVariable Long id, @Valid @RequestBody Lead lead) {
        try {
            if (!leadService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            lead.setId(id);
            Lead updatedLead = leadService.save(lead);
            log.info("Updated lead with id: {}", id);
            return ResponseEntity.ok(updatedLead);
        } catch (Exception e) {
            log.error("Error updating lead with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete a lead
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        try {
            if (!leadService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            leadService.deleteById(id);
            log.info("Deleted lead with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting lead with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Qualify a lead using AI
     */
    @PostMapping("/{id}/qualify")
    public ResponseEntity<LeadQualificationService.LeadQualificationResult> qualifyLead(@PathVariable Long id) {
        try {
            Optional<Lead> leadOpt = leadService.findById(id);
            if (!leadOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            LeadQualificationService.LeadQualificationResult result = qualificationService.qualifyLead(leadOpt.get());
            
            // Update the lead with the qualification results
            Lead lead = leadOpt.get();
            lead.setQualificationScore(result.getScore());
            lead.setQualificationReasoning(result.getReasoning());
            leadService.save(lead);
            
            log.info("Qualified lead {} with score: {}", id, result.getScore());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error qualifying lead with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Re-qualify a lead with custom criteria
     */
    @PostMapping("/{id}/requalify")
    public ResponseEntity<LeadQualificationService.LeadQualificationResult> requalifyLead(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> customCriteria) {
        try {
            Optional<Lead> leadOpt = leadService.findById(id);
            if (!leadOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            LeadQualificationService.LeadQualificationResult result = 
                qualificationService.requalifyLead(leadOpt.get(), customCriteria);
            
            // Update the lead with the new qualification results
            Lead lead = leadOpt.get();
            lead.setQualificationScore(result.getScore());
            lead.setQualificationReasoning(result.getReasoning());
            leadService.save(lead);
            
            log.info("Re-qualified lead {} with score: {}", id, result.getScore());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error re-qualifying lead with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Generate a personalized email message
     */
    @PostMapping("/{id}/messages/email")
    public ResponseEntity<Map<String, String>> generateEmailMessage(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        try {
            Optional<Lead> leadOpt = leadService.findById(id);
            if (!leadOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            String messageType = request.getOrDefault("messageType", "initial outreach");
            String message = messagingService.generateEmailMessage(leadOpt.get(), messageType);
            
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("Error generating email message for lead {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Generate a personalized LinkedIn message
     */
    @PostMapping("/{id}/messages/linkedin")
    public ResponseEntity<Map<String, String>> generateLinkedInMessage(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        try {
            Optional<Lead> leadOpt = leadService.findById(id);
            if (!leadOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            String messageType = request.getOrDefault("messageType", "connection request");
            String message = messagingService.generateLinkedInMessage(leadOpt.get(), messageType);
            
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("Error generating LinkedIn message for lead {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update lead pipeline stage
     */
    @PutMapping("/{id}/pipeline-stage")
    public ResponseEntity<Lead> updatePipelineStage(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        try {
            Optional<Lead> leadOpt = leadService.findById(id);
            if (!leadOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            PipelineStage newStage = PipelineStage.valueOf(request.get("pipelineStage"));
            Lead lead = leadOpt.get();
            lead.setPipelineStage(newStage);
            
            Lead updatedLead = leadService.save(lead);
            log.info("Updated pipeline stage for lead {} to {}", id, newStage);
            return ResponseEntity.ok(updatedLead);
        } catch (Exception e) {
            log.error("Error updating pipeline stage for lead {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get leads by pipeline stage
     */
    @GetMapping("/pipeline/{stage}")
    public ResponseEntity<List<Lead>> getLeadsByPipelineStage(@PathVariable PipelineStage stage) {
        try {
            List<Lead> leads = leadService.findByPipelineStage(stage);
            return ResponseEntity.ok(leads);
        } catch (Exception e) {
            log.error("Error retrieving leads for pipeline stage {}", stage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get leads needing follow-up
     */
    @GetMapping("/follow-up")
    public ResponseEntity<List<Lead>> getLeadsNeedingFollowUp() {
        try {
            List<Lead> leads = leadService.findLeadsNeedingFollowUp();
            return ResponseEntity.ok(leads);
        } catch (Exception e) {
            log.error("Error retrieving leads needing follow-up", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get pipeline statistics
     */
    @GetMapping("/stats/pipeline")
    public ResponseEntity<Map<String, Long>> getPipelineStats() {
        try {
            Map<String, Long> stats = leadService.getPipelineStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error retrieving pipeline statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}