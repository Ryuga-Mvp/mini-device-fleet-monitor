package com.example.fleet_monitor.controller;

import com.example.fleet_monitor.dto.DeviceResponse;
import com.example.fleet_monitor.dto.FleetSummaryResponse;
import com.example.fleet_monitor.dto.HeartbeatRequest;
import com.example.fleet_monitor.dto.RegisterDeviceRequest;
import com.example.fleet_monitor.exception.DeviceNotFoundException;
import com.example.fleet_monitor.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Test
    void shouldRegisterDevice() throws Exception {

        DeviceResponse response = new DeviceResponse(
                "device-01",
                "Lab Device 01",
                "OFFLINE",
                null
        );

        when(deviceService.registerDevice(any(RegisterDeviceRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/devices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "id": "device-01",
                                "name": "Lab Device 01"
                            }
                            """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("device-01"))
                .andExpect(jsonPath("$.name").value("Lab Device 01"))
                .andExpect(jsonPath("$.status").value("OFFLINE"));
    }

    @Test
    void shouldSendHeartbeat() throws Exception {

        DeviceResponse response = new DeviceResponse(
                "device-01",
                "Lab Device 01",
                "ONLINE",
                Instant.parse("2026-09-30T10:00:00Z")
        );

        when(deviceService.recordHeartbeat(
                eq("device-01"),
                any(HeartbeatRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/devices/device-01/heartbeat")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "timestamp": "2026-09-30T10:00:00Z",
                                "status": "OK"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("device-01"))
                .andExpect(jsonPath("$.status").value("ONLINE"));
    }

    @Test
    void shouldGetDevice() throws Exception {

        DeviceResponse response = new DeviceResponse(
                "device-01",
                "Lab Device 01",
                "ONLINE",
                Instant.parse("2026-09-30T10:00:00Z")
        );

        when(deviceService.getDevice("device-01"))
                .thenReturn(response);

        mockMvc.perform(
                        get("/devices/device-01")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("device-01"))
                .andExpect(jsonPath("$.name").value("Lab Device 01"));
    }

    @Test
    void shouldListDevices() throws Exception {

        List<DeviceResponse> responses = List.of(
                new DeviceResponse(
                        "device-01",
                        "Lab Device 01",
                        "OFFLINE",
                        null
                ),
                new DeviceResponse(
                        "device-02",
                        "Lab Device 02",
                        "OFFLINE",
                        null
                )
        );

        when(deviceService.getAllDevices())
                .thenReturn(responses);

        mockMvc.perform(
                        get("/devices")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnFleetSummary() throws Exception {

        FleetSummaryResponse response =
                new FleetSummaryResponse(2, 0, 2);

        when(deviceService.getSummary())
                .thenReturn(response);

        mockMvc.perform(
                        get("/summary")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.online").value(0))
                .andExpect(jsonPath("$.offline").value(2));
    }

    @Test
    void shouldReturnNotFoundForUnknownDevice() throws Exception {

        when(deviceService.getDevice("does-not-exist"))
                .thenThrow(
                        new DeviceNotFoundException("does-not-exist")
                );

        mockMvc.perform(
                        get("/devices/does-not-exist")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldRejectInvalidDevice() throws Exception {

        mockMvc.perform(
                        post("/devices")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "id": "",
                                "name": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }


}