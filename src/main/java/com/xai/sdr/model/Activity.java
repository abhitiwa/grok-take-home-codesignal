package com.xai.sdr.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing an activity or interaction with a lead.
 * 
 * Tracks all communications, meetings, and other touchpoints
 * throughout the sales process for comprehensive history.
 */
@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    @NotNull(message = "Lead is required")
    private Lead lead;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    @NotNull(message = "Activity type is required")
    private ActivityType activityType;
    
    @NotBlank(message = "Activity description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "outcome")
    private String outcome;
    
    @Column(name = "next_steps", columnDefinition = "TEXT")
    private String nextSteps;
    
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (completedDate == null && activityType == ActivityType.CALL) {
            completedDate = LocalDateTime.now();
        }
    }
}