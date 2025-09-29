package com.xai.sdr.model;

/**
 * Enumeration representing the different stages in the sales pipeline.
 * 
 * Each stage represents a specific phase in the lead qualification and
 * conversion process, allowing for systematic tracking and progression.
 */
public enum PipelineStage {
    
    /**
     * Newly created lead that hasn't been contacted yet
     */
    NEW("New Lead"),
    
    /**
     * Initial contact has been made, awaiting response
     */
    CONTACTED("Contacted"),
    
    /**
     * Lead has responded and is showing interest
     */
    QUALIFIED("Qualified"),
    
    /**
     * Lead is actively engaged in the sales process
     */
    ENGAGED("Engaged"),
    
    /**
     * Lead has been scheduled for a meeting or demo
     */
    MEETING_SCHEDULED("Meeting Scheduled"),
    
    /**
     * Lead has been successfully converted to a customer
     */
    CONVERTED("Converted"),
    
    /**
     * Lead is no longer active or interested
     */
    CLOSED_LOST("Closed Lost");
    
    private final String displayName;
    
    PipelineStage(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get the next logical stage in the pipeline progression
     */
    public PipelineStage getNextStage() {
        switch (this) {
            case NEW:
                return CONTACTED;
            case CONTACTED:
                return QUALIFIED;
            case QUALIFIED:
                return ENGAGED;
            case ENGAGED:
                return MEETING_SCHEDULED;
            case MEETING_SCHEDULED:
                return CONVERTED;
            default:
                return this; // No progression from final stages
        }
    }
    
    /**
     * Check if this stage represents an active lead
     */
    public boolean isActive() {
        return this != CONVERTED && this != CLOSED_LOST;
    }
}