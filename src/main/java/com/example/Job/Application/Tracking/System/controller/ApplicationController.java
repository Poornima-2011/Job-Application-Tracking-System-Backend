package com.example.Job.Application.Tracking.System.controller;

import com.example.Job.Application.Tracking.System.dto.ApplicationRequestDTO;
import com.example.Job.Application.Tracking.System.dto.ApplicationResponseDTO;
import com.example.Job.Application.Tracking.System.dto.ApplicationStatusUpdateDTO;
import com.example.Job.Application.Tracking.System.service.ApplicationService;
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
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponseDTO> apply(@Valid @RequestBody ApplicationRequestDTO dto) {
        ApplicationResponseDTO created = applicationService.applyToJob(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getByCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(applicationService.getApplicationsByCandidate(candidateId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponseDTO>> getByJob(
            @PathVariable Long jobId,
            @RequestParam Long recruiterId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId, recruiterId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateDTO dto) {
        return ResponseEntity.ok(applicationService.updateStatus(id, dto));
    }
}