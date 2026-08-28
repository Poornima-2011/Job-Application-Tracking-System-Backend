package com.example.Job.Application.Tracking.System.repository;
import com.example.Job.Application.Tracking.System.entity.Job;
import com.example.Job.Application.Tracking.System.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(JobStatus status);

    List<Job> findByTitleContainingIgnoreCaseAndStatus(String title, JobStatus status);

    List<Job> findByRecruiterId(Long recruiterId);
}