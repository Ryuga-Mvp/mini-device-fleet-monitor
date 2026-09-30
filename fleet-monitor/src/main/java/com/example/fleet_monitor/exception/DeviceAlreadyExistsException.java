package com.example.fleet_monitor.exception;

public class DeviceAlreadyExistsException extends RuntimeException {

    public DeviceAlreadyExistsException(String deviceId) {
        super("Device already exists: " + deviceId);
    }
}
