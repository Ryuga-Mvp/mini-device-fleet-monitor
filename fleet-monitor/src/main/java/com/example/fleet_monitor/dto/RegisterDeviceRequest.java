package com.example.fleet_monitor.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceRequest(

        @NotBlank(message = "Device id is required")
        String id,

        @NotBlank(message = "Device name is required")
        String name

) {
}
