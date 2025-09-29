package com.xai.sdr.service;

import com.xai.sdr.model.Lead;
import com.xai.sdr.model.PipelineStage;
import com.xai.sdr.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for lead management operations.
 * 
 * Provides business logic for lead CRUD operations, search functionality,
 * pipeline management, and statistical reporting.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LeadService {
    
    private final LeadRepository leadRepository;
    
    /**
     * Find all leads
     */
    @Transactional(readOnly = true)
    public List<Lead> findAll() {
        return leadRepository.findAll();
    }
    
    /**
     * Find lead by ID
     */
    @Transactional(readOnly = true)
    public Optional<Lead> findById(Long id) {
        return leadRepository.findById(id);
    }
    
    /**
     * Save a lead (create or update)
     */
    public Lead save(Lead lead) {
        return leadRepository.save(lead);
    }
    
    /**
     * Delete a lead by ID
     */
    public void deleteById(Long id) {
        leadRepository.deleteById(id);
    }
    
    /**
     * Check if lead exists by ID
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return leadRepository.existsById(id);
    }
    
    /**
     * Find lead by email
     */
    @Transactional(readOnly = true)
    public Optional<Lead> findByEmail(String email) {
        return leadRepository.findByEmail(email);
    }
    
    /**
     * Find leads by pipeline stage
     */
    @Transactional(readOnly = true)
    public List<Lead> findByPipelineStage(PipelineStage pipelineStage) {
        return leadRepository.findByPipelineStage(pipelineStage);
    }
    
    /**
     * Find leads by company name
     */
    @Transactional(readOnly = true)
    public List<Lead> findByCompanyName(String companyName) {
        return leadRepository.findByCompanyNameIgnoreCaseContaining(companyName);
    }
    
    /**
     * Find leads by industry
     */
    @Transactional(readOnly = true)
    public List<Lead> findByIndustry(String industry) {
        return leadRepository.findByIndustry(industry);
    }
    
    /**
     * Find leads needing follow-up
     */
    @Transactional(readOnly = true)
    public List<Lead> findLeadsNeedingFollowUp() {
        List<PipelineStage> activeStages = List.of(
            PipelineStage.NEW, 
            PipelineStage.CONTACTED, 
            PipelineStage.QUALIFIED, 
            PipelineStage.ENGAGED
        );
        return leadRepository.findLeadsNeedingFollowUp(LocalDateTime.now(), activeStages);
    }
    
    /**
     * Find leads created within date range
     */
    @Transactional(readOnly = true)
    public List<Lead> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return leadRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    /**
     * Find leads with qualification score above threshold
     */
    @Transactional(readOnly = true)
    public List<Lead> findByQualificationScoreGreaterThan(Integer minScore) {
        return leadRepository.findByQualificationScoreGreaterThan(minScore);
    }
    
    /**
     * Search leads with multiple criteria
     */
    @Transactional(readOnly = true)
    public List<Lead> searchLeads(String firstName, String lastName, String companyName, 
                                 String industry, PipelineStage pipelineStage) {
        return leadRepository.searchLeads(firstName, lastName, companyName, industry, pipelineStage);
    }
    
    /**
     * Get pipeline statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getPipelineStats() {
        Map<String, Long> stats = new HashMap<>();
        
        for (PipelineStage stage : PipelineStage.values()) {
            long count = leadRepository.countByPipelineStage(stage);
            stats.put(stage.name(), count);
        }
        
        return stats;
    }
    
    /**
     * Get leads with no activities
     */
    @Transactional(readOnly = true)
    public List<Lead> findLeadsWithNoActivities() {
        return leadRepository.findLeadsWithNoActivities();
    }
    
    /**
     * Update lead's last contact date
     */
    public Lead updateLastContactDate(Long leadId) {
        Optional<Lead> leadOpt = findById(leadId);
        if (leadOpt.isPresent()) {
            Lead lead = leadOpt.get();
            lead.setLastContactDate(LocalDateTime.now());
            return save(lead);
        }
        throw new RuntimeException("Lead not found with id: " + leadId);
    }
    
    /**
     * Update lead's next follow-up date
     */
    public Lead updateNextFollowUpDate(Long leadId, LocalDateTime nextFollowUpDate) {
        Optional<Lead> leadOpt = findById(leadId);
        if (leadOpt.isPresent()) {
            Lead lead = leadOpt.get();
            lead.setNextFollowUpDate(nextFollowUpDate);
            return save(lead);
        }
        throw new RuntimeException("Lead not found with id: " + leadId);
    }
    
    /**
     * Advance lead to next pipeline stage
     */
    public Lead advanceToNextStage(Long leadId) {
        Optional<Lead> leadOpt = findById(leadId);
        if (leadOpt.isPresent()) {
            Lead lead = leadOpt.get();
            PipelineStage nextStage = lead.getPipelineStage().getNextStage();
            if (nextStage != lead.getPipelineStage()) {
                lead.setPipelineStage(nextStage);
                log.info("Advanced lead {} from {} to {}", leadId, lead.getPipelineStage(), nextStage);
                return save(lead);
            }
            return lead;
        }
        throw new RuntimeException("Lead not found with id: " + leadId);
    }
    
    /**
     * Get high-priority leads (score > 80)
     */
    @Transactional(readOnly = true)
    public List<Lead> getHighPriorityLeads() {
        return findByQualificationScoreGreaterThan(80);
    }
    
    /**
     * Get leads by score range
     */
    @Transactional(readOnly = true)
    public List<Lead> getLeadsByScoreRange(Integer minScore, Integer maxScore) {
        List<Lead> allLeads = findAll();
        return allLeads.stream()
                .filter(lead -> lead.getQualificationScore() != null)
                .filter(lead -> lead.getQualificationScore() >= minScore && lead.getQualificationScore() <= maxScore)
                .collect(java.util.stream.Collectors.toList());
    }
}