package com.photoanalyzer.model;

import java.io.Serializable;

public class Recommendation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String category;
    private String suggestion;
    private String reason;
    private String priority; // "high", "medium", "low"

    public Recommendation() {}

    public Recommendation(String category, String suggestion, String reason, String priority) {
        this.category = category;
        this.suggestion = suggestion;
        this.reason = reason;
        this.priority = priority;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
