package com.example.fleet_monitor.service;

import com.example.fleet_monitor.dto.DeviceResponse;
import com.example.fleet_monitor.dto.FleetSummaryResponse;
import com.example.fleet_monitor.dto.HeartbeatRequest;
import com.example.fleet_monitor.dto.RegisterDeviceRequest;
import com.example.fleet_monitor.exception.DeviceAlreadyExistsException;
import com.example.fleet_monitor.exception.DeviceNotFoundException;
import com.example.fleet_monitor.model.Device;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceService {

    private static final long TIMEOUT_SECONDS = 30;

    private final ConcurrentHashMap<String, Device> devices =
            new ConcurrentHashMap<>();

    private final Clock clock;

    public DeviceService() {
        this(Clock.systemUTC());
    }

    public DeviceService(Clock clock) {
        this.clock = clock;
    }

    public DeviceResponse registerDevice(
            RegisterDeviceRequest request) {

        Device device = new Device(
                request.id(),
                request.name()
        );

        Device existing = devices.putIfAbsent(
                request.id(),
                device
        );

        if (existing != null) {
            throw new DeviceAlreadyExistsException(request.id());
        }

        return toResponse(device);
    }

    public DeviceResponse recordHeartbeat(
            String deviceId,
            HeartbeatRequest request) {

        Device device = devices.get(deviceId);

        if (device == null) {
            throw new DeviceNotFoundException(deviceId);
        }

        device.setLastHeartbeat(request.timestamp());
        device.setLastHeartbeatStatus(request.status());

        return toResponse(device);
    }

    public List<DeviceResponse> getAllDevices() {

        return devices.values()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DeviceResponse getDevice(String deviceId) {

        Device device = devices.get(deviceId);

        if (device == null) {
            throw new DeviceNotFoundException(deviceId);
        }

        return toResponse(device);
    }

    public FleetSummaryResponse getSummary() {

        int total = devices.size();

        int online = (int) devices.values()
                .stream()
                .filter(this::isOnline)
                .count();

        int offline = total - online;

        return new FleetSummaryResponse(
                total,
                online,
                offline
        );
    }

    private boolean isOnline(Device device) {

        if (device.getLastHeartbeat() == null) {
            return false;
        }

        Instant now = Instant.now(clock);

        long secondsSinceHeartbeat =
                Duration.between(
                        device.getLastHeartbeat(),
                        now
                ).getSeconds();

        return secondsSinceHeartbeat <= TIMEOUT_SECONDS;
    }

    private DeviceResponse toResponse(Device device) {

        String status = isOnline(device)
                ? "ONLINE"
                : "OFFLINE";

        return new DeviceResponse(
                device.getId(),
                device.getName(),
                status,
                device.getLastHeartbeat()
        );
    }


}