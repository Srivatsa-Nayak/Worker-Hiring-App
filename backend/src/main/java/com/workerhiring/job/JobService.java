package com.workerhiring.job;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workerhiring.application.ApplicationRepository;
import com.workerhiring.application.JobApplication;
import com.workerhiring.common.ApiException;
import com.workerhiring.job.JobDtos.CategoryOption;
import com.workerhiring.job.JobDtos.JobRequest;
import com.workerhiring.job.JobDtos.JobResponse;
import com.workerhiring.job.JobDtos.Poster;
import com.workerhiring.notification.NotificationService;
import com.workerhiring.user.User;
import com.workerhiring.user.UserRepository;

@Service
public class JobService {

    private final JobRepository jobs;
    private final ApplicationRepository applications;
    private final UserRepository users;
    private final NotificationService notifications;

    public JobService(JobRepository jobs, ApplicationRepository applications, UserRepository users,
                      NotificationService notifications) {
        this.jobs = jobs;
        this.applications = applications;
        this.users = users;
        this.notifications = notifications;
    }

    public List<CategoryOption> categories() {
        return Arrays.stream(JobCategory.values()).map(c -> new CategoryOption(c, c.getLabel())).toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> search(String q, String location, JobCategory category, Long viewerId) {
        List<Job> found = jobs.search(Job.Status.OPEN, likePattern(q), likePattern(location), category);
        return toResponses(found, viewerId);
    }

    @Transactional(readOnly = true)
    public JobResponse get(Long id, Long viewerId) {
        // Closed ads stay viewable (links get shared); the UI marks them closed and blocks applying.
        Job job = jobs.findWithPoster(id).orElseThrow(() -> ApiException.notFound("Job"));
        return toResponses(List.of(job), viewerId).getFirst();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> mine(Long userId) {
        return toResponses(jobs.findByPoster(userId), userId);
    }

    @Transactional
    public JobResponse create(JobRequest req, Long userId) {
        validateStartDate(req.startDate(), null);
        User poster = users.findById(userId).orElseThrow(() -> ApiException.unauthorized("Please log in to continue"));
        Job job = new Job();
        job.setPostedBy(poster);
        apply(job, req);
        jobs.save(job);
        return toResponses(List.of(job), userId).getFirst();
    }

    @Transactional
    public JobResponse update(Long id, JobRequest req, Long userId) {
        Job job = ownedJob(id, userId);
        validateStartDate(req.startDate(), job.getStartDate());
        apply(job, req);
        return toResponses(List.of(job), userId).getFirst();
    }

    @Transactional
    public JobResponse setStatus(Long id, Job.Status status, Long userId) {
        Job job = ownedJob(id, userId);
        job.setStatus(status);
        return toResponses(List.of(job), userId).getFirst();
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Job job = ownedJob(id, userId);
        String title = job.getTitle();
        for (Long applicantId : applications.findApplicantIdsByJob(id)) {
            users.findById(applicantId).ifPresent(u -> notifications.notify(u,
                    "The ad \"" + title + "\" you applied to was removed by the recruiter.", "/applications"));
        }
        applications.deleteByJobId(id);
        jobs.delete(job);
    }

    Job ownedJob(Long id, Long userId) {
        Job job = jobs.findWithPoster(id).orElseThrow(() -> ApiException.notFound("Job"));
        if (!job.getPostedBy().getId().equals(userId)) {
            throw ApiException.forbidden("Only the person who posted this ad can change it");
        }
        return job;
    }

    private static void validateStartDate(LocalDate startDate, LocalDate existing) {
        // Allow keeping an existing (possibly past) date when editing; new dates can't be in the past.
        if (startDate.isBefore(LocalDate.now()) && !startDate.equals(existing)) {
            throw ApiException.badRequest("Start date can't be in the past");
        }
    }

    private static void apply(Job job, JobRequest req) {
        job.setTitle(req.title().trim());
        job.setCategory(req.category());
        job.setLocation(req.location().trim());
        job.setSalaryAmount(req.salaryAmount());
        job.setSalaryPeriod(req.salaryPeriod());
        job.setDescription(req.description().trim());
        job.setRequirements(req.requirements().trim());
        job.setStartDate(req.startDate());
        job.setOpenings(req.openings());
        job.setContactPhone(req.contactPhone().trim());
        job.setContactEmail(req.contactEmail().trim());
    }

    private static String likePattern(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        String escaped = s.trim().toLowerCase().replace("\\", "").replace("%", "").replace("_", "");
        return escaped.isEmpty() ? null : "%" + escaped + "%";
    }

    private List<JobResponse> toResponses(List<Job> list, Long viewerId) {
        if (list.isEmpty()) {
            return List.of();
        }
        List<Long> ids = list.stream().map(Job::getId).toList();
        Map<Long, Long> counts = new HashMap<>();
        for (Object[] row : applications.countByJobIds(ids)) {
            counts.put((Long) row[0], (Long) row[1]);
        }
        Map<Long, JobApplication> mine = viewerId == null ? Map.of()
                : applications.findByApplicantAndJobs(viewerId, ids).stream()
                .collect(Collectors.toMap(a -> a.getJob().getId(), Function.identity()));

        return list.stream().map(j -> {
            boolean signedIn = viewerId != null;
            boolean owner = signedIn && j.getPostedBy().getId().equals(viewerId);
            JobApplication my = mine.get(j.getId());
            return new JobResponse(j.getId(), j.getTitle(), j.getCategory(), j.getCategory().getLabel(),
                    j.getLocation(), j.getSalaryAmount(), j.getSalaryPeriod(), j.getDescription(),
                    j.getRequirements(), j.getStartDate(), j.getOpenings(),
                    signedIn ? j.getContactPhone() : null, signedIn ? j.getContactEmail() : null,
                    j.getStatus(), j.getCreatedAt(),
                    new Poster(j.getPostedBy().getId(), j.getPostedBy().getFullName()),
                    counts.getOrDefault(j.getId(), 0L), owner,
                    my == null ? null : my.getStatus(), my == null ? null : my.getId());
        }).toList();
    }
}
