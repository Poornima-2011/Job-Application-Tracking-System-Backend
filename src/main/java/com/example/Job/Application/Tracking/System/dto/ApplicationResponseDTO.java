package com.example.Job.Application.Tracking.System.dto;
import com.example.Job.Application.Tracking.System.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationResponseDTO {
    private Long id;
    private String jobTitle;
    private String candidateName;
    private ApplicationStatus status;
    private LocalDate appliedDate;
}