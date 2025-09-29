package com.xai.sdr.repository;

import com.xai.sdr.model.Activity;
import com.xai.sdr.model.ActivityType;
import com.xai.sdr.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Activity entity operations.
 * 
 * Provides methods for querying and managing lead activities
 * and interactions throughout the sales process.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    /**
     * Find all activities for a specific lead
     */
    List<Activity> findByLeadOrderByCreatedAtDesc(Lead lead);
    
    /**
     * Find activities by type
     */
    List<Activity> findByActivityType(ActivityType activityType);
    
    /**
     * Find activities created within a date range
     */
    List<Activity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find activities for a lead by type
     */
    List<Activity> findByLeadAndActivityType(Lead lead, ActivityType activityType);
    
    /**
     * Find recent activities across all leads
     */
    @Query("SELECT a FROM Activity a ORDER BY a.createdAt DESC")
    List<Activity> findRecentActivities();
    
    /**
     * Find activities scheduled for a specific date
     */
    @Query("SELECT a FROM Activity a WHERE DATE(a.scheduledDate) = DATE(:date)")
    List<Activity> findActivitiesScheduledForDate(@Param("date") LocalDateTime date);
    
    /**
     * Find overdue activities (scheduled in the past but not completed)
     */
    @Query("SELECT a FROM Activity a WHERE a.scheduledDate < :currentDate AND a.completedDate IS NULL")
    List<Activity> findOverdueActivities(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Count activities by type for a specific lead
     */
    long countByLeadAndActivityType(Lead lead, ActivityType activityType);
    
    /**
     * Find the last activity for a lead
     */
    @Query("SELECT a FROM Activity a WHERE a.lead = :lead ORDER BY a.createdAt DESC")
    List<Activity> findLastActivityForLead(@Param("lead") Lead lead);
}