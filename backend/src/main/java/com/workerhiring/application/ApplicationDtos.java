package com.workerhiring.application;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.workerhiring.job.Job;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class ApplicationDtos {

    private ApplicationDtos() {
    }

    public record ApplyRequest(@Size(max = 1000, message = "Keep your message under 1000 characters") String message) {
    }

    public record UpdateStatusRequest(
            @NotNull(message = "Status is required") JobApplication.Status status,
            LocalDateTime interviewAt,
            @Size(max = 1000, message = "Keep the note under 1000 characters") String recruiterNote) {
    }

    /** What the recruiter sees about an applicant. */
    public record Applicant(Long id, String name, String phone, String email, String address, String skills) {
    }

    /** Summary of the job, for the applicant's "My applications" list. */
    public record JobSummary(Long id, String title, String location, Integer salaryAmount,
                             Job.SalaryPeriod salaryPeriod, LocalDate startDate, Job.Status status,
                             String postedByName, String contactPhone, String contactEmail) {
    }

    public record ApplicationResponse(Long id, Long jobId, JobApplication.Status status, String message,
                                      LocalDateTime interviewAt, String recruiterNote, Instant createdAt,
                                      Instant updatedAt, Applicant applicant, JobSummary job) {
    }
}
