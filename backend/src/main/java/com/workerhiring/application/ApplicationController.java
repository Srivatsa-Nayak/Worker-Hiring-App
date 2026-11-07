package com.workerhiring.application;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.workerhiring.application.ApplicationDtos.ApplicationResponse;
import com.workerhiring.application.ApplicationDtos.ApplyRequest;
import com.workerhiring.application.ApplicationDtos.UpdateStatusRequest;
import com.workerhiring.auth.CurrentUser;

import jakarta.validation.Valid;

@RestController
public class ApplicationController {

    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @PostMapping("/api/jobs/{jobId:\\d+}/applications")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable Long jobId,
                                                     @Valid @RequestBody(required = false) ApplyRequest req) {
        String message = req == null ? null : req.message();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.apply(jobId, CurrentUser.id(), message));
    }

    /** Applicants for one of the signed-in user's ads. */
    @GetMapping("/api/jobs/{jobId:\\d+}/applications")
    public List<ApplicationResponse> forJob(@PathVariable Long jobId) {
        return service.forJob(jobId, CurrentUser.id());
    }

    @GetMapping("/api/applications/mine")
    public List<ApplicationResponse> mine() {
        return service.mine(CurrentUser.id());
    }

    @PatchMapping("/api/applications/{id:\\d+}")
    public ApplicationResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest req) {
        return service.updateStatus(id, CurrentUser.id(), req);
    }

    @DeleteMapping("/api/applications/{id:\\d+}")
    public ResponseEntity<Void> withdraw(@PathVariable Long id) {
        service.withdraw(id, CurrentUser.id());
        return ResponseEntity.noContent().build();
    }
}
