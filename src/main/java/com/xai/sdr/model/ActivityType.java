package com.xai.sdr.model;

/**
 * Enumeration representing different types of activities that can be
 * performed with leads during the sales process.
 */
public enum ActivityType {
    
    /**
     * Initial outreach via email
     */
    EMAIL("Email"),
    
    /**
     * Phone call with the lead
     */
    CALL("Phone Call"),
    
    /**
     * LinkedIn message or connection
     */
    LINKEDIN("LinkedIn"),
    
    /**
     * In-person or virtual meeting
     */
    MEETING("Meeting"),
    
    /**
     * Product demonstration
     */
    DEMO("Demo"),
    
    /**
     * Follow-up communication
     */
    FOLLOW_UP("Follow-up"),
    
    /**
     * Proposal or quote sent
     */
    PROPOSAL("Proposal"),
    
    /**
     * Contract negotiation
     */
    NEGOTIATION("Negotiation"),
    
    /**
     * General note or observation
     */
    NOTE("Note");
    
    private final String displayName;
    
    ActivityType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}