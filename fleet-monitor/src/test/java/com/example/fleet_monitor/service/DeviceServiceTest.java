package com.example.fleet_monitor.service;

import com.example.fleet_monitor.dto.DeviceResponse;
import com.example.fleet_monitor.dto.FleetSummaryResponse;
import com.example.fleet_monitor.dto.HeartbeatRequest;
import com.example.fleet_monitor.dto.RegisterDeviceRequest;
import com.example.fleet_monitor.exception.DeviceAlreadyExistsException;
import com.example.fleet_monitor.exception.DeviceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class DeviceServiceTest {

    private DeviceService deviceService;

    private final Instant currentTime =
            Instant.parse("2026-09-30T10:00:00Z");

    @BeforeEach
    void setUp() {

        Clock fixedClock =
                Clock.fixed(
                        currentTime,
                        ZoneOffset.UTC
                );

        deviceService =
                new DeviceService(fixedClock);
    }

    @Test
    void shouldRegisterDevice() {

        DeviceResponse response =
                deviceService.registerDevice(
                        new RegisterDeviceRequest(
                                "device-01",
                                "Lab Device 01"
                        )
                );

        assertEquals("device-01", response.id());
        assertEquals("Lab Device 01", response.name());
        assertEquals("OFFLINE", response.status());
        assertNull(response.lastHeartbeat());
    }

    @Test
    void shouldRecordHeartbeat() {

        deviceService.registerDevice(
                new RegisterDeviceRequest(
                        "device-01",
                        "Lab Device 01"
                )
        );

        Instant heartbeatTime =
                currentTime.minusSeconds(10);

        DeviceResponse response =
                deviceService.recordHeartbeat(
                        "device-01",
                        new HeartbeatRequest(
                                heartbeatTime,
                                "OK"
                        )
                );

        assertEquals("device-01", response.id());
        assertEquals("ONLINE", response.status());
        assertEquals(
                heartbeatTime,
                response.lastHeartbeat()
        );
    }

    @Test
    void shouldMarkDeviceOnlineWithin30Seconds() {

        deviceService.registerDevice(
                new RegisterDeviceRequest(
                        "device-01",
                        "Lab Device 01"
                )
        );

        Instant heartbeat =
                currentTime.minusSeconds(30);

        deviceService.recordHeartbeat(
                "device-01",
                new HeartbeatRequest(
                        heartbeat,
                        "OK"
                )
        );

        DeviceResponse response =
                deviceService.getDevice("device-01");

        assertEquals("ONLINE", response.status());
    }

    @Test
    void shouldMarkDeviceOfflineAfter30Seconds() {

        deviceService.registerDevice(
                new RegisterDeviceRequest(
                        "device-01",
                        "Lab Device 01"
                )
        );

        Instant heartbeat =
                currentTime.minusSeconds(31);

        deviceService.recordHeartbeat(
                "device-01",
                new HeartbeatRequest(
                        heartbeat,
                        "OK"
                )
        );

        DeviceResponse response =
                deviceService.getDevice("device-01");

        assertEquals("OFFLINE", response.status());
    }

    @Test
    void shouldReturnAllDevices() {

        deviceService.registerDevice(
                new RegisterDeviceRequest(
                        "device-01",
                        "Lab Device 01"
                )
        );

        deviceService.registerDevice(
                new RegisterDeviceRequest(
                        "device-02",
                        "Lab Device 02"
                )
        );

        List<DeviceResponse> devices =
                deviceService.getAllDevices();

        assertEquals(2, devices.size());
    }

    @Test
    void shouldReturnFleetSummary() {

        deviceService.registerDevice(
                new RegisterDeviceRequest(
                        "device-01",
                        "Lab Device 01"
                )
        );

        deviceService.registerDevice(
                new RegisterDeviceRequest(
                        "device-02",
                        "Lab Device 02"
                )
        );

        deviceService.recordHeartbeat(
                "device-01",
                new HeartbeatRequest(
                        currentTime,
                        "OK"
                )
        );

        FleetSummaryResponse summary =
                deviceService.getSummary();

        assertEquals(2, summary.total());
        assertEquals(1, summary.online());
        assertEquals(1, summary.offline());
    }

    @Test
    void shouldThrowExceptionForUnknownDevice() {

        assertThrows(
                DeviceNotFoundException.class,
                () -> deviceService.getDevice(
                        "unknown-device"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenHeartbeatDeviceDoesNotExist() {

        assertThrows(
                DeviceNotFoundException.class,
                () -> deviceService.recordHeartbeat(
                        "unknown-device",
                        new HeartbeatRequest(
                                currentTime,
                                "OK"
                        )
                )
        );
    }

    @Test
    void shouldRejectDuplicateDevice() {

        RegisterDeviceRequest request =
                new RegisterDeviceRequest(
                        "device-01",
                        "Lab Device 01"
                );

        deviceService.registerDevice(request);

        assertThrows(
                DeviceAlreadyExistsException.class,
                () -> deviceService.registerDevice(request)
        );
    }
}
