package com.example.Job.Application.Tracking.System.service;

import com.example.Job.Application.Tracking.System.dto.ApplicationRequestDTO;
import com.example.Job.Application.Tracking.System.dto.ApplicationResponseDTO;
import com.example.Job.Application.Tracking.System.dto.ApplicationStatusUpdateDTO;
import com.example.Job.Application.Tracking.System.entity.Application;
import com.example.Job.Application.Tracking.System.entity.Job;
import com.example.Job.Application.Tracking.System.entity.User;
import com.example.Job.Application.Tracking.System.enums.ApplicationStatus;
import com.example.Job.Application.Tracking.System.enums.JobStatus;
import com.example.Job.Application.Tracking.System.enums.Role;
import com.example.Job.Application.Tracking.System.exception.*;
import com.example.Job.Application.Tracking.System.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final JobService jobService;

    private static final Map<ApplicationStatus, Set<ApplicationStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(ApplicationStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(ApplicationStatus.APPLIED, EnumSet.of(ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED));
        ALLOWED_TRANSITIONS.put(ApplicationStatus.SHORTLISTED, EnumSet.of(ApplicationStatus.HIRED, ApplicationStatus.REJECTED));
        ALLOWED_TRANSITIONS.put(ApplicationStatus.HIRED, EnumSet.noneOf(ApplicationStatus.class));
        ALLOWED_TRANSITIONS.put(ApplicationStatus.REJECTED, EnumSet.noneOf(ApplicationStatus.class));
    }

    public ApplicationService(ApplicationRepository applicationRepository,
                              UserService userService,
                              JobService jobService) {
        this.applicationRepository = applicationRepository;
        this.userService = userService;
        this.jobService = jobService;
    }

    @Transactional
    public ApplicationResponseDTO applyToJob(ApplicationRequestDTO dto) {
        Job job = jobService.findJobOrThrow(dto.getJobId());
        User candidate = userService.findUserOrThrow(dto.getCandidateId());

        if (candidate.getRole() != Role.CANDIDATE) {
            throw new InvalidRoleException("Only users with role CANDIDATE can apply to jobs");
        }

        if (job.getStatus() == JobStatus.CLOSED) {
            throw new JobClosedException("This job is closed and no longer accepting applications");
        }

        if (job.getDeadline().isBefore(LocalDate.now())) {
            throw new JobClosedException("The application deadline for this job has passed");
        }

        if (applicationRepository.existsByJobIdAndCandidateId(job.getId(), candidate.getId())) {
            throw new DuplicateApplicationException("You have already applied to this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setCandidate(candidate);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedDate(LocalDate.now());

        Application saved = applicationRepository.save(application);
        return toResponseDTO(saved);
    }

    public List<ApplicationResponseDTO> getApplicationsByCandidate(Long candidateId) {
        userService.findUserOrThrow(candidateId);

        return applicationRepository.findByCandidateId(candidateId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ApplicationResponseDTO> getApplicationsByJob(Long jobId, Long recruiterId) {
        Job job = jobService.findJobOrThrow(jobId);

        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new InvalidRoleException("Only the recruiter who posted this job can view its applicants");
        }

        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponseDTO updateStatus(Long applicationId, ApplicationStatusUpdateDTO dto) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        ApplicationStatus currentStatus = application.getStatus();
        ApplicationStatus targetStatus = dto.getStatus();

        Set<ApplicationStatus> allowedNextStatuses = ALLOWED_TRANSITIONS.get(currentStatus);

        if (!allowedNextStatuses.contains(targetStatus)) {
            throw new InvalidStatusTransitionException(
                    "Cannot move application from " + currentStatus + " to " + targetStatus);
        }

        application.setStatus(targetStatus);
        Application updated = applicationRepository.save(application);
        return toResponseDTO(updated);
    }

    private ApplicationResponseDTO toResponseDTO(Application application) {
        return new ApplicationResponseDTO(
                application.getId(),
                application.getJob().getTitle(),
                application.getCandidate().getName(),
                application.getStatus(),
                application.getAppliedDate()
        );
    }
}