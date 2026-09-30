package com.example.fleet_monitor.dto;

public record FleetSummaryResponse(
        int total,
        int online,
        int offline
) {
}
