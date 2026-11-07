package com.workerhiring.job;

import java.time.Instant;
import java.time.LocalDate;

import com.workerhiring.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/** A work advertisement posted by a recruiter (shop owner). */
@Entity
@Table(name = "job")
public class Job {

    public enum Status { OPEN, CLOSED }

    public enum SalaryPeriod { HOUR, DAY, WEEK, MONTH }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private JobCategory category;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(nullable = false)
    private Integer salaryAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SalaryPeriod salaryPeriod;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 2000)
    private String requirements;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private Integer openings;

    @Column(nullable = false, length = 20)
    private String contactPhone;

    @Column(nullable = false, length = 120)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.OPEN;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "posted_by_id")
    private User postedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public JobCategory getCategory() { return category; }
    public void setCategory(JobCategory category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getSalaryAmount() { return salaryAmount; }
    public void setSalaryAmount(Integer salaryAmount) { this.salaryAmount = salaryAmount; }
    public SalaryPeriod getSalaryPeriod() { return salaryPeriod; }
    public void setSalaryPeriod(SalaryPeriod salaryPeriod) { this.salaryPeriod = salaryPeriod; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public Integer getOpenings() { return openings; }
    public void setOpenings(Integer openings) { this.openings = openings; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public User getPostedBy() { return postedBy; }
    public void setPostedBy(User postedBy) { this.postedBy = postedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
