Mini Device Fleet Monitor

A small Spring Boot application that monitors a simulated fleet of devices.

Each registered device periodically sends a heartbeat. The application stores the latest heartbeat and automatically determines whether a device is ONLINE or OFFLINE based on the heartbeat timeout.

Features

Register a device

Receive device heartbeats

List all registered devices

Get details for a specific device

View fleet summary

Automatic ONLINE/OFFLINE status

30-second heartbeat timeout

Python simulator for 5 devices

Unit and controller tests

Technology

Java 21

Spring Boot

Maven

Python 3

JUnit

MockMvc

Project Structure
mini-device-fleet-monitor/
├── fleet-monitor/
│   ├── pom.xml
│   ├── README.md
│   └── src/
│       ├── main/
│       │   └── java/
│       └── test/
│           └── java/
│
└── simulator/
└── simulator.py

How the Application Works

The application keeps registered devices in memory.

When a device sends a heartbeat, the application stores the timestamp of the latest heartbeat.

A device is considered:

ONLINE — heartbeat received within the last 30 seconds

OFFLINE — no heartbeat received for more than 30 seconds

The status is calculated automatically whenever device information is requested.

Running the Spring Boot Application

Open PowerShell and navigate to the Spring Boot project:

cd C:\Users\HP\Downloads\fleet-monitor\fleet-monitor


Run the application using Maven Wrapper:

.\mvnw.cmd spring-boot:run


The application starts on:

http://localhost:8080


Alternatively, the application can be started directly from IntelliJ IDEA by running FleetMonitorApplication.

Running Tests

From the fleet-monitor directory:

.\mvnw.cmd test


The test suite covers device registration, heartbeat handling, device lookup, fleet summary, validation, duplicate devices, unknown devices, and timeout behavior.

Device Simulator

The project includes a Python simulator that simulates five devices:

device-01
device-02
device-03
device-04
device-05


Each device sends a heartbeat every 5 seconds.

Install Python dependency

From the simulator directory:

cd C:\Users\HP\Downloads\fleet-monitor\simulator


Install the required package:

python -m pip install requests

Start the simulator

Make sure the Spring Boot application is running first.

Then run:

python simulator.py


The simulator provides commands such as:

stop device-01
start device-01
status
exit


For example, to demonstrate the timeout behavior:

> stop device-01


After stopping the device, wait more than 30 seconds. The application will report device-01 as OFFLINE.

Start it again with:

> start device-01


The device will begin sending heartbeats again and will become ONLINE.

API Endpoints
Register a Device
POST /devices
Content-Type: application/json


Request:

{
"id": "device-01",
"name": "Lab Device 01"
}


Example response:

{
"id": "device-01",
"name": "Lab Device 01",
"status": "OFFLINE",
"lastHeartbeat": null
}

Send a Heartbeat
POST /devices/{id}/heartbeat
Content-Type: application/json


Request:

{
"timestamp": "2026-09-30T10:00:00Z",
"status": "OK"
}


Example:

POST /devices/device-01/heartbeat

List Devices
GET /devices


Example response:

[
{
"id": "device-01",
"name": "Lab Device 01",
"status": "ONLINE",
"lastHeartbeat": "2026-09-30T10:00:00Z"
}
]

Get Device Details
GET /devices/{id}


Example:

GET /devices/device-01

Fleet Summary
GET /summary


Example response:

{
"total": 5,
"online": 4,
"offline": 1
}

Error Handling

The application handles common errors such as:

Registering a device with an existing ID → 409 Conflict

Requesting an unknown device → 404 Not Found

Sending a heartbeat for an unknown device → 404 Not Found

Invalid request data → 400 Bad Request

Design Notes

The application intentionally uses in-memory storage because this project is designed as a small three-hour engineering exercise.

A ConcurrentHashMap is used for thread-safe access because multiple simulated devices can send heartbeats concurrently.

The service uses Clock injection for time calculations, which makes the 30-second timeout behavior easier to test deterministically.

AI Usage

AI tools were used as an engineering assistant during development for:

Discussing project structure and implementation approaches

Reviewing Java/Spring Boot code

Identifying compilation and test issues

Improving test coverage

Troubleshooting Maven and Git setup

Reviewing documentation

The final implementation was tested locally and adjusted based on the application's actual behavior.

Limitations

Device data is stored only in memory and is lost when the application restarts.

Authentication and authorization are not implemented.

The simulator and application communicate over localhost.

No persistent database is required for this exercise.

Author

GitHub: https://github.com/Ryuga-Mvp