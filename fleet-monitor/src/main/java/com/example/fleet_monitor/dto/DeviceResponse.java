package com.example.fleet_monitor.dto;

import java.time.Instant;

public record DeviceResponse(
        String id,
        String name,
        String status,
        Instant lastHeartbeat
) {
}
