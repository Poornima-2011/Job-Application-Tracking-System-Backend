package com.example.Job.Application.Tracking.System.controller;
import com.example.Job.Application.Tracking.System.dto.JobRequestDTO;
import com.example.Job.Application.Tracking.System.dto.JobResponseDTO;
import com.example.Job.Application.Tracking.System.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponseDTO> createJob(@Valid @RequestBody JobRequestDTO dto) {
        JobResponseDTO created = jobService.createJob(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<JobResponseDTO>> getOpenJobs() {
        return ResponseEntity.ok(jobService.getOpenJobs());
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobResponseDTO>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(jobService.searchJobs(title, location));
    }

    @GetMapping("/recruiter/{recruiterId}")
    public ResponseEntity<List<JobResponseDTO>> getJobsByRecruiter(@PathVariable Long recruiterId) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiterId));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<JobResponseDTO> closeJob(
            @PathVariable Long id,
            @RequestParam Long recruiterId) {
        return ResponseEntity.ok(jobService.closeJob(id, recruiterId));
    }
}