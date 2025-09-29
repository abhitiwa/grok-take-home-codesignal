package com.xai.sdr.service;

import com.xai.sdr.model.Activity;
import com.xai.sdr.model.ActivityType;
import com.xai.sdr.model.Lead;
import com.xai.sdr.repository.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for activity management operations.
 * 
 * Provides business logic for activity CRUD operations, reporting,
 * and lead interaction tracking throughout the sales process.
 */
@Service
@Transactional
public class ActivityService {
    
    private static final Logger log = LoggerFactory.getLogger(ActivityService.class);
    
    private final ActivityRepository activityRepository;
    
    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
    
    /**
     * Find all activities
     */
    @Transactional(readOnly = true)
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }
    
    /**
     * Find activity by ID
     */
    @Transactional(readOnly = true)
    public Optional<Activity> findById(Long id) {
        return activityRepository.findById(id);
    }
    
    /**
     * Save an activity (create or update)
     */
    public Activity save(Activity activity) {
        return activityRepository.save(activity);
    }
    
    /**
     * Delete an activity by ID
     */
    public void deleteById(Long id) {
        activityRepository.deleteById(id);
    }
    
    /**
     * Check if activity exists by ID
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return activityRepository.existsById(id);
    }
    
    /**
     * Find activities by lead
     */
    @Transactional(readOnly = true)
    public List<Activity> findByLead(Lead lead) {
        return activityRepository.findByLeadOrderByCreatedAtDesc(lead);
    }
    
    /**
     * Find activities by type
     */
    @Transactional(readOnly = true)
    public List<Activity> findByActivityType(String activityType) {
        try {
            ActivityType type = ActivityType.valueOf(activityType.toUpperCase());
            return activityRepository.findByActivityType(type);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid activity type: {}", activityType);
            return List.of();
        }
    }
    
    /**
     * Find activities by type for a specific lead
     */
    @Transactional(readOnly = true)
    public List<Activity> findByLeadAndActivityType(Lead lead, ActivityType activityType) {
        return activityRepository.findByLeadAndActivityType(lead, activityType);
    }
    
    /**
     * Find recent activities across all leads
     */
    @Transactional(readOnly = true)
    public List<Activity> findRecentActivities() {
        return activityRepository.findRecentActivities();
    }
    
    /**
     * Find activities created within date range
     */
    @Transactional(readOnly = true)
    public List<Activity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return activityRepository.findByCreatedAtBetween(startDate, endDate);
    }
    
    /**
     * Find overdue activities
     */
    @Transactional(readOnly = true)
    public List<Activity> findOverdueActivities() {
        return activityRepository.findOverdueActivities(LocalDateTime.now());
    }
    
    /**
     * Find activities scheduled for a specific date
     */
    @Transactional(readOnly = true)
    public List<Activity> findActivitiesScheduledForDate(LocalDateTime date) {
        return activityRepository.findActivitiesScheduledForDate(date);
    }
    
    /**
     * Count activities by type for a lead
     */
    @Transactional(readOnly = true)
    public long countByLeadAndActivityType(Lead lead, ActivityType activityType) {
        return activityRepository.countByLeadAndActivityType(lead, activityType);
    }
    
    /**
     * Get the last activity for a lead
     */
    @Transactional(readOnly = true)
    public Optional<Activity> findLastActivityForLead(Lead lead) {
        List<Activity> activities = activityRepository.findLastActivityForLead(lead);
        return activities.isEmpty() ? Optional.empty() : Optional.of(activities.get(0));
    }
    
    /**
     * Get activity statistics for a lead
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getActivityStats(Lead lead) {
        Map<String, Object> stats = new HashMap<>();
        
        // Total activities
        List<Activity> allActivities = findByLead(lead);
        stats.put("totalActivities", allActivities.size());
        
        // Activities by type
        Map<String, Long> activitiesByType = new HashMap<>();
        for (ActivityType type : ActivityType.values()) {
            long count = countByLeadAndActivityType(lead, type);
            if (count > 0) {
                activitiesByType.put(type.name(), count);
            }
        }
        stats.put("activitiesByType", activitiesByType);
        
        // Last activity date
        Optional<Activity> lastActivity = findLastActivityForLead(lead);
        if (lastActivity.isPresent()) {
            stats.put("lastActivityDate", lastActivity.get().getCreatedAt());
            stats.put("lastActivityType", lastActivity.get().getActivityType().name());
        }
        
        // Completed vs pending activities
        long completedActivities = allActivities.stream()
                .filter(activity -> activity.getCompletedDate() != null)
                .count();
        stats.put("completedActivities", completedActivities);
        stats.put("pendingActivities", allActivities.size() - completedActivities);
        
        // Activities this month
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Activity> thisMonthActivities = findByCreatedAtBetween(startOfMonth, LocalDateTime.now());
        stats.put("activitiesThisMonth", thisMonthActivities.size());
        
        return stats;
    }
    
    /**
     * Create a follow-up activity
     */
    public Activity createFollowUpActivity(Lead lead, String description, LocalDateTime scheduledDate) {
        Activity followUp = new Activity();
        followUp.setLead(lead);
        followUp.setActivityType(ActivityType.FOLLOW_UP);
        followUp.setDescription(description);
        followUp.setScheduledDate(scheduledDate);
        followUp.setCreatedBy("System");
        
        return save(followUp);
    }
    
    /**
     * Create a note activity
     */
    public Activity createNoteActivity(Lead lead, String description, String createdBy) {
        Activity note = new Activity();
        note.setLead(lead);
        note.setActivityType(ActivityType.NOTE);
        note.setDescription(description);
        note.setCreatedBy(createdBy);
        
        return save(note);
    }
    
    /**
     * Get activities needing attention (overdue or scheduled for today)
     */
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesNeedingAttention() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Activity> overdue = findOverdueActivities();
        List<Activity> todayScheduled = findActivitiesScheduledForDate(now);
        
        // Combine and remove duplicates
        Map<Long, Activity> uniqueActivities = new HashMap<>();
        overdue.forEach(activity -> uniqueActivities.put(activity.getId(), activity));
        todayScheduled.forEach(activity -> uniqueActivities.put(activity.getId(), activity));
        
        return List.copyOf(uniqueActivities.values());
    }
    
    /**
     * Get activity summary for dashboard
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getActivitySummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Total activities today
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        List<Activity> todayActivities = findByCreatedAtBetween(startOfDay, endOfDay);
        summary.put("activitiesToday", todayActivities.size());
        
        // Overdue activities
        List<Activity> overdueActivities = findOverdueActivities();
        summary.put("overdueActivities", overdueActivities.size());
        
        // Activities needing attention
        List<Activity> attentionNeeded = getActivitiesNeedingAttention();
        summary.put("activitiesNeedingAttention", attentionNeeded.size());
        
        // Recent activities (last 10)
        List<Activity> recentActivities = findRecentActivities();
        summary.put("recentActivities", recentActivities.stream().limit(10).collect(java.util.stream.Collectors.toList()));
        
        return summary;
    }
}