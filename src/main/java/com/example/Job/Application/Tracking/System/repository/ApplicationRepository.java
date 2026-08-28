package com.example.Job.Application.Tracking.System.repository;


import com.example.Job.Application.Tracking.System.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByCandidateId(Long candidateId);

    List<Application> findByJobId(Long jobId);

    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);
}