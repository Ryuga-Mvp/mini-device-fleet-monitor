package com.example.fleet_monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record HeartbeatRequest(

        @NotNull(message = "Timestamp is required")
        Instant timestamp,

        @NotBlank(message = "Status is required")
        String status

) {
}
