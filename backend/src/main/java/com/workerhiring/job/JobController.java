package com.workerhiring.job;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workerhiring.auth.CurrentUser;
import com.workerhiring.job.JobDtos.CategoryOption;
import com.workerhiring.job.JobDtos.JobRequest;
import com.workerhiring.job.JobDtos.JobResponse;
import com.workerhiring.job.JobDtos.StatusRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /** Public list of open ads, newest first. */
    @GetMapping
    public List<JobResponse> search(@RequestParam(required = false) String q,
                                    @RequestParam(required = false) String location,
                                    @RequestParam(required = false) JobCategory category) {
        return jobService.search(q, location, category, CurrentUser.idIfPresent().orElse(null));
    }

    @GetMapping("/categories")
    public List<CategoryOption> categories() {
        return jobService.categories();
    }

    @GetMapping("/mine")
    public List<JobResponse> mine() {
        return jobService.mine(CurrentUser.id());
    }

    @GetMapping("/{id:\\d+}")
    public JobResponse get(@PathVariable Long id) {
        return jobService.get(id, CurrentUser.idIfPresent().orElse(null));
    }

    @PostMapping
    public ResponseEntity<JobResponse> create(@Valid @RequestBody JobRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(req, CurrentUser.id()));
    }

    @PutMapping("/{id:\\d+}")
    public JobResponse update(@PathVariable Long id, @Valid @RequestBody JobRequest req) {
        return jobService.update(id, req, CurrentUser.id());
    }

    @PatchMapping("/{id:\\d+}/status")
    public JobResponse setStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return jobService.setStatus(id, req.status(), CurrentUser.id());
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobService.delete(id, CurrentUser.id());
        return ResponseEntity.noContent().build();
    }
}
