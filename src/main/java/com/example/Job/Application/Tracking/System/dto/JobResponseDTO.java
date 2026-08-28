package com.example.Job.Application.Tracking.System.dto;
import com.example.Job.Application.Tracking.System.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDate deadline;
    private JobStatus status;
    private String recruiterName;
}