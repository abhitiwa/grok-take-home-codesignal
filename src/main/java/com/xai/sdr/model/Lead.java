package com.xai.sdr.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a sales lead in the system.
 * 
 * Contains all relevant information about a potential customer including
 * contact details, qualification scores, and pipeline status.
 */
@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "company_size")
    private String companySize;
    
    @Column(name = "industry")
    private String industry;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "qualification_score")
    private Integer qualificationScore;
    
    @Column(name = "qualification_reasoning", columnDefinition = "TEXT")
    private String qualificationReasoning;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "pipeline_stage")
    private PipelineStage pipelineStage;
    
    @Column(name = "last_contact_date")
    private LocalDateTime lastContactDate;
    
    @Column(name = "next_follow_up_date")
    private LocalDateTime nextFollowUpDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Activity> activities = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (pipelineStage == null) {
            pipelineStage = PipelineStage.NEW;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}