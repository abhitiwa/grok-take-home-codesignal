package com.xai.sdr.controller;

import com.xai.sdr.model.Activity;
import com.xai.sdr.model.Lead;
import com.xai.sdr.service.ActivityService;
import com.xai.sdr.service.LeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for activity management operations.
 * 
 * Provides endpoints for managing lead activities, interactions,
 * and communication history throughout the sales process.
 */
@RestController
@RequestMapping("/activities")
@CrossOrigin(origins = "*")
public class ActivityController {
    
    private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
    
    private final ActivityService activityService;
    private final LeadService leadService;
    
    @Autowired
    public ActivityController(ActivityService activityService, LeadService leadService) {
        this.activityService = activityService;
        this.leadService = leadService;
    }
    
    /**
     * Get all activities
     */
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        try {
            List<Activity> activities = activityService.findAll();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error retrieving activities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get a specific activity by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        try {
            Optional<Activity> activity = activityService.findById(id);
            return activity.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error retrieving activity with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get activities for a specific lead
     */
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<Activity>> getActivitiesByLead(@PathVariable Long leadId) {
        try {
            Optional<Lead> leadOpt = leadService.findById(leadId);
            if (!leadOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            List<Activity> activities = activityService.findByLead(leadOpt.get());
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error retrieving activities for lead {}", leadId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create a new activity
     */
    @PostMapping
    public ResponseEntity<Activity> createActivity(@Valid @RequestBody Activity activity) {
        try {
            // Verify the lead exists
            if (activity.getLead() == null || activity.getLead().getId() == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Optional<Lead> leadOpt = leadService.findById(activity.getLead().getId());
            if (!leadOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            activity.setLead(leadOpt.get());
            Activity savedActivity = activityService.save(activity);
            
            // Update lead's last contact date if this is a contact activity
            if (activity.getActivityType().name().contains("CALL") || 
                activity.getActivityType().name().contains("EMAIL") ||
                activity.getActivityType().name().contains("MEETING")) {
                leadService.updateLastContactDate(activity.getLead().getId());
            }
            
            log.info("Created new activity with id: {} for lead: {}", savedActivity.getId(), activity.getLead().getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedActivity);
        } catch (Exception e) {
            log.error("Error creating activity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update an existing activity
     */
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @Valid @RequestBody Activity activity) {
        try {
            if (!activityService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            activity.setId(id);
            Activity updatedActivity = activityService.save(activity);
            log.info("Updated activity with id: {}", id);
            return ResponseEntity.ok(updatedActivity);
        } catch (Exception e) {
            log.error("Error updating activity with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete an activity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        try {
            if (!activityService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            activityService.deleteById(id);
            log.info("Deleted activity with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting activity with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get recent activities across all leads
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Activity>> getRecentActivities() {
        try {
            List<Activity> activities = activityService.findRecentActivities();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error retrieving recent activities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get activities by type
     */
    @GetMapping("/type/{activityType}")
    public ResponseEntity<List<Activity>> getActivitiesByType(@PathVariable String activityType) {
        try {
            List<Activity> activities = activityService.findByActivityType(activityType);
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error retrieving activities by type {}", activityType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get overdue activities
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Activity>> getOverdueActivities() {
        try {
            List<Activity> activities = activityService.findOverdueActivities();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error retrieving overdue activities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Mark activity as completed
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Activity> completeActivity(@PathVariable Long id) {
        try {
            Optional<Activity> activityOpt = activityService.findById(id);
            if (!activityOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Activity activity = activityOpt.get();
            activity.setCompletedDate(java.time.LocalDateTime.now());
            Activity updatedActivity = activityService.save(activity);
            
            log.info("Marked activity {} as completed", id);
            return ResponseEntity.ok(updatedActivity);
        } catch (Exception e) {
            log.error("Error completing activity {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get activity statistics for a lead
     */
    @GetMapping("/lead/{leadId}/stats")
    public ResponseEntity<Object> getActivityStats(@PathVariable Long leadId) {
        try {
            Optional<Lead> leadOpt = leadService.findById(leadId);
            if (!leadOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Object stats = activityService.getActivityStats(leadOpt.get());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error retrieving activity stats for lead {}", leadId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}