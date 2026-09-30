package com.example.fleet_monitor.controller;

import com.example.fleet_monitor.dto.DeviceResponse;
import com.example.fleet_monitor.dto.FleetSummaryResponse;
import com.example.fleet_monitor.dto.HeartbeatRequest;
import com.example.fleet_monitor.dto.RegisterDeviceRequest;
import com.example.fleet_monitor.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/devices")
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceResponse registerDevice(
            @Valid @RequestBody RegisterDeviceRequest request) {
        return deviceService.registerDevice(request);
    }

    @PostMapping("/devices/{id}/heartbeat")
    public DeviceResponse heartbeat(
            @PathVariable String id,
            @Valid @RequestBody HeartbeatRequest request) {
        return deviceService.recordHeartbeat(id, request);
    }

    @GetMapping("/devices")
    public List<DeviceResponse> getDevices() {
        return deviceService.getAllDevices();
    }

    @GetMapping("/devices/{id}")
    public DeviceResponse getDevice(@PathVariable String id) {
        return deviceService.getDevice(id);
    }

    @GetMapping("/summary")
    public FleetSummaryResponse getSummary() {
        return deviceService.getSummary();
    }


}