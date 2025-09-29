package com.xai.sdr.repository;

import com.xai.sdr.model.Lead;
import com.xai.sdr.model.PipelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Lead entity operations.
 * 
 * Provides custom query methods for lead management including
 * search, filtering, and pipeline stage operations.
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    
    /**
     * Find lead by email address
     */
    Optional<Lead> findByEmail(String email);
    
    /**
     * Find all leads in a specific pipeline stage
     */
    List<Lead> findByPipelineStage(PipelineStage pipelineStage);
    
    /**
     * Find leads by company name (case insensitive)
     */
    List<Lead> findByCompanyNameIgnoreCaseContaining(String companyName);
    
    /**
     * Find leads by industry
     */
    List<Lead> findByIndustry(String industry);
    
    /**
     * Find leads that need follow-up (next follow-up date is in the past)
     */
    @Query("SELECT l FROM Lead l WHERE l.nextFollowUpDate <= :currentDate AND l.pipelineStage IN :activeStages")
    List<Lead> findLeadsNeedingFollowUp(@Param("currentDate") LocalDateTime currentDate, 
                                       @Param("activeStages") List<PipelineStage> activeStages);
    
    /**
     * Find leads created within a date range
     */
    List<Lead> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find leads with qualification score above threshold
     */
    List<Lead> findByQualificationScoreGreaterThan(Integer minScore);
    
    /**
     * Search leads by multiple criteria
     */
    @Query("SELECT l FROM Lead l WHERE " +
           "(:firstName IS NULL OR LOWER(l.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(l.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:companyName IS NULL OR LOWER(l.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))) AND " +
           "(:industry IS NULL OR l.industry = :industry) AND " +
           "(:pipelineStage IS NULL OR l.pipelineStage = :pipelineStage)")
    List<Lead> searchLeads(@Param("firstName") String firstName,
                          @Param("lastName") String lastName,
                          @Param("companyName") String companyName,
                          @Param("industry") String industry,
                          @Param("pipelineStage") PipelineStage pipelineStage);
    
    /**
     * Count leads by pipeline stage
     */
    long countByPipelineStage(PipelineStage pipelineStage);
    
    /**
     * Find leads with no activities
     */
    @Query("SELECT l FROM Lead l WHERE l.activities IS EMPTY")
    List<Lead> findLeadsWithNoActivities();
}