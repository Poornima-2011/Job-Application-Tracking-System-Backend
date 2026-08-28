package com.example.Job.Application.Tracking.System.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequestDTO {

    @NotNull(message = "jobId is required")
    private Long jobId;

    @NotNull(message = "candidateId is required")
    private Long candidateId;
}