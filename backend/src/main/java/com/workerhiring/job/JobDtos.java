package com.workerhiring.job;

import java.time.Instant;
import java.time.LocalDate;

import com.workerhiring.application.JobApplication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class JobDtos {

    private JobDtos() {
    }

    public record JobRequest(
            @NotBlank(message = "Work name is required") @Size(max = 100, message = "Keep the work name under 100 characters") String title,
            @NotNull(message = "Choose a category") JobCategory category,
            @NotBlank(message = "Work location is required") @Size(max = 150) String location,
            @NotNull(message = "Salary is required") @Min(value = 1, message = "Salary must be more than 0") @Max(value = 10_000_000, message = "Salary looks too large") Integer salaryAmount,
            @NotNull(message = "Choose how often the salary is paid") Job.SalaryPeriod salaryPeriod,
            @NotBlank(message = "Work description is required") @Size(max = 2000) String description,
            @NotBlank(message = "Work requirements are required") @Size(max = 2000) String requirements,
            @NotNull(message = "Start date is required") LocalDate startDate,
            @NotNull(message = "Number of openings is required") @Min(value = 1, message = "At least 1 opening") @Max(value = 500, message = "At most 500 openings") Integer openings,
            @NotBlank(message = "Phone number is required") @Pattern(regexp = "^\\+?[0-9 ]{10,15}$", message = "Enter a valid phone number (10 to 15 digits)") String contactPhone,
            @NotBlank(message = "Email is required") @Email(message = "Enter a valid email") String contactEmail) {
    }

    public record StatusRequest(@NotNull(message = "Status is required") Job.Status status) {
    }

    public record Poster(Long id, String name) {
    }

    public record CategoryOption(JobCategory value, String label) {
    }

    /**
     * Contact details are only filled in for signed-in viewers. {@code myApplicationStatus} is the viewer's
     * own application status for this ad, or null if they haven't applied.
     */
    public record JobResponse(Long id, String title, JobCategory category, String categoryLabel, String location,
                              Integer salaryAmount, Job.SalaryPeriod salaryPeriod, String description,
                              String requirements, LocalDate startDate, Integer openings, String contactPhone,
                              String contactEmail, Job.Status status, Instant createdAt, Poster postedBy,
                              long applicationCount, boolean owner,
                              JobApplication.Status myApplicationStatus, Long myApplicationId) {
    }
}
