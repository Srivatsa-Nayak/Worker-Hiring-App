package com.workerhiring.application;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workerhiring.application.ApplicationDtos.Applicant;
import com.workerhiring.application.ApplicationDtos.ApplicationResponse;
import com.workerhiring.application.ApplicationDtos.JobSummary;
import com.workerhiring.application.ApplicationDtos.UpdateStatusRequest;
import com.workerhiring.common.ApiException;
import com.workerhiring.job.Job;
import com.workerhiring.job.JobRepository;
import com.workerhiring.notification.NotificationService;
import com.workerhiring.user.User;
import com.workerhiring.user.UserRepository;

@Service
public class ApplicationService {

    private static final DateTimeFormatter INTERVIEW_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy 'at' h:mm a");

    private final ApplicationRepository applications;
    private final JobRepository jobs;
    private final UserRepository users;
    private final NotificationService notifications;

    public ApplicationService(ApplicationRepository applications, JobRepository jobs, UserRepository users,
                              NotificationService notifications) {
        this.applications = applications;
        this.jobs = jobs;
        this.users = users;
        this.notifications = notifications;
    }

    @Transactional
    public ApplicationResponse apply(Long jobId, Long userId, String message) {
        Job job = jobs.findWithPoster(jobId).orElseThrow(() -> ApiException.notFound("Job"));
        if (job.getPostedBy().getId().equals(userId)) {
            throw ApiException.badRequest("You can't apply to your own ad");
        }
        if (job.getStatus() != Job.Status.OPEN) {
            throw ApiException.badRequest("This ad is closed and no longer taking applications");
        }
        if (applications.findByJobIdAndApplicantId(jobId, userId).isPresent()) {
            throw ApiException.conflict("You have already applied for this work");
        }
        User applicant = users.findById(userId).orElseThrow(() -> ApiException.unauthorized("Please log in to continue"));

        JobApplication a = new JobApplication();
        a.setJob(job);
        a.setApplicant(applicant);
        a.setMessage(message == null || message.isBlank() ? null : message.trim());
        applications.save(a);

        notifications.notify(job.getPostedBy(),
                applicant.getFullName() + " applied for \"" + job.getTitle() + "\".",
                "/my-ads/" + job.getId());
        return toResponse(a, true, true);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> mine(Long userId) {
        return applications.findByApplicant(userId).stream().map(a -> toResponse(a, false, true)).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> forJob(Long jobId, Long userId) {
        Job job = jobs.findWithPoster(jobId).orElseThrow(() -> ApiException.notFound("Job"));
        if (!job.getPostedBy().getId().equals(userId)) {
            throw ApiException.forbidden("Only the person who posted this ad can see its applicants");
        }
        return applications.findByJob(jobId).stream().map(a -> toResponse(a, true, false)).toList();
    }

    @Transactional
    public ApplicationResponse updateStatus(Long applicationId, Long userId, UpdateStatusRequest req) {
        JobApplication a = applications.findFull(applicationId).orElseThrow(() -> ApiException.notFound("Application"));
        Job job = a.getJob();
        if (!job.getPostedBy().getId().equals(userId)) {
            throw ApiException.forbidden("Only the person who posted this ad can update applicants");
        }
        if (req.status() == JobApplication.Status.INTERVIEW && req.interviewAt() == null) {
            throw ApiException.badRequest("Pick a date and time for the interview");
        }

        a.setStatus(req.status());
        a.setInterviewAt(req.status() == JobApplication.Status.INTERVIEW ? req.interviewAt() : null);
        a.setRecruiterNote(req.recruiterNote() == null || req.recruiterNote().isBlank() ? null : req.recruiterNote().trim());

        String title = "\"" + job.getTitle() + "\"";
        String text = switch (req.status()) {
            case APPLIED -> "Your application for " + title + " was moved back to new.";
            case CALL_LATER -> job.getPostedBy().getFullName() + " will call you later about " + title + ".";
            case INTERVIEW -> "Interview for " + title + " on " + req.interviewAt().format(INTERVIEW_FORMAT) + ".";
            case HIRED -> "You're hired for " + title + "! Contact " + job.getPostedBy().getFullName() + " to get started.";
            case REJECTED -> "Your application for " + title + " was not selected this time.";
        };
        notifications.notify(a.getApplicant(), text, "/applications");
        return toResponse(a, true, false);
    }

    @Transactional
    public void withdraw(Long applicationId, Long userId) {
        JobApplication a = applications.findFull(applicationId).orElseThrow(() -> ApiException.notFound("Application"));
        if (!a.getApplicant().getId().equals(userId)) {
            throw ApiException.forbidden("You can only withdraw your own applications");
        }
        notifications.notify(a.getJob().getPostedBy(),
                a.getApplicant().getFullName() + " withdrew their application for \"" + a.getJob().getTitle() + "\".",
                "/my-ads/" + a.getJob().getId());
        applications.delete(a);
    }

    private static ApplicationResponse toResponse(JobApplication a, boolean includeApplicant, boolean includeJob) {
        Applicant applicant = null;
        if (includeApplicant) {
            User u = a.getApplicant();
            applicant = new Applicant(u.getId(), u.getFullName(), u.getPhone(), u.getEmail(), u.getAddress(), u.getSkills());
        }
        JobSummary job = null;
        if (includeJob) {
            Job j = a.getJob();
            job = new JobSummary(j.getId(), j.getTitle(), j.getLocation(), j.getSalaryAmount(), j.getSalaryPeriod(),
                    j.getStartDate(), j.getStatus(), j.getPostedBy().getFullName(), j.getContactPhone(), j.getContactEmail());
        }
        return new ApplicationResponse(a.getId(), a.getJob().getId(), a.getStatus(), a.getMessage(),
                a.getInterviewAt(), a.getRecruiterNote(), a.getCreatedAt(), a.getUpdatedAt(), applicant, job);
    }
}
