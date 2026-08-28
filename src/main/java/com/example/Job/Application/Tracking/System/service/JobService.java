package com.example.Job.Application.Tracking.System.service;
import com.example.Job.Application.Tracking.System.dto.JobRequestDTO;
import com.example.Job.Application.Tracking.System.dto.JobResponseDTO;
import com.example.Job.Application.Tracking.System.entity.Job;
import com.example.Job.Application.Tracking.System.entity.User;
import com.example.Job.Application.Tracking.System.enums.JobStatus;
import com.example.Job.Application.Tracking.System.enums.Role;
import com.example.Job.Application.Tracking.System.exception.InvalidRoleException;
import com.example.Job.Application.Tracking.System.exception.ResourceNotFoundException;
import com.example.Job.Application.Tracking.System.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserService userService;

    public JobService(JobRepository jobRepository, UserService userService) {
        this.jobRepository = jobRepository;
        this.userService = userService;
    }

    public JobResponseDTO createJob(JobRequestDTO dto) {
        User recruiter = userService.findUserOrThrow(dto.getRecruiterId());

        if (recruiter.getRole() != Role.RECRUITER) {
            throw new InvalidRoleException("Only users with role RECRUITER can post a job");
        }

        Job job = new Job();
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setLocation(dto.getLocation());
        job.setDeadline(dto.getDeadline());
        job.setStatus(JobStatus.OPEN);
        job.setRecruiter(recruiter);

        Job saved = jobRepository.save(job);
        return toResponseDTO(saved);
    }

    public List<JobResponseDTO> getOpenJobs() {
        return jobRepository.findByStatus(JobStatus.OPEN)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<JobResponseDTO> searchJobs(String title, String location) {
        String keyword = (title == null) ? "" : title;

        return jobRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, JobStatus.OPEN)
                .stream()
                .filter(job -> location == null || location.isBlank()
                        || job.getLocation().equalsIgnoreCase(location))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<JobResponseDTO> getJobsByRecruiter(Long recruiterId) {
        return jobRepository.findByRecruiterId(recruiterId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public JobResponseDTO closeJob(Long jobId, Long recruiterId) {
        Job job = findJobOrThrow(jobId);

        if (!job.getRecruiter().getId().equals(recruiterId)) {
            throw new InvalidRoleException("Only the recruiter who posted this job can close it");
        }

        job.setStatus(JobStatus.CLOSED);
        Job updated = jobRepository.save(job);
        return toResponseDTO(updated);
    }

    Job findJobOrThrow(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    private JobResponseDTO toResponseDTO(Job job) {
        return new JobResponseDTO(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getDeadline(),
                job.getStatus(),
                job.getRecruiter().getName()
        );
    }
}