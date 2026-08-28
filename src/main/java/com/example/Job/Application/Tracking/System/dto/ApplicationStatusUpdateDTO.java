package com.example.Job.Application.Tracking.System.dto;

import com.example.Job.Application.Tracking.System.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationStatusUpdateDTO {

    @NotNull(message = "status is required")
    private ApplicationStatus status;
}