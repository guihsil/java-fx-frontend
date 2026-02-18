package com.freelamarket.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProjectDTO {
    private String title;
    private String description;
    private Double budget;
    private String status;
    private LocalDateTime deadline;

    public ProjectDTO(String title, String description, Double budget, LocalDate deadlineDate) {
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.status = "OPEN";
        this.deadline = deadlineDate.atStartOfDay();
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Double getBudget() { return budget; }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDeadline() { return deadline; }
}