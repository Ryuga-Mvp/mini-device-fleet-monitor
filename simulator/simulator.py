import requests
import threading
import time
from datetime import datetime, timezone

BASE_URL = "http://localhost:8080"
HEARTBEAT_INTERVAL = 5

devices = {
"device-01": "Lab Device 01",
"device-02": "Lab Device 02",
"device-03": "Lab Device 03",
"device-04": "Lab Device 04",
"device-05": "Lab Device 05",
}

running = {device_id: True for device_id in devices}

def register_device(device_id, name):
try:
response = requests.post(
f"{BASE_URL}/devices",
json={
"id": device_id,
"name": name
},
timeout=5
)

    if response.status_code == 201:
        print(f"{device_id} registered")
    elif response.status_code == 409:
        print(f"{device_id} already registered")
    else:
        print(
            f"{device_id} registration failed: "
            f"{response.status_code}"
        )

except requests.RequestException as e:
    print(f"{device_id} registration error: {e}")


def send_heartbeat(device_id):
try:
timestamp = datetime.now(timezone.utc).isoformat()

    response = requests.post(
        f"{BASE_URL}/devices/{device_id}/heartbeat",
        json={
            "timestamp": timestamp,
            "status": "OK"
        },
        timeout=5
    )

    if response.ok:
        print(f"{device_id} -> heartbeat sent")
    else:
        print(
            f"{device_id} -> heartbeat failed: "
            f"{response.status_code}"
        )

except requests.RequestException as e:
    print(f"{device_id} -> heartbeat error: {e}")


def simulate_device(device_id):
while True:
if running[device_id]:
send_heartbeat(device_id)

    time.sleep(HEARTBEAT_INTERVAL)


def show_status():
print("\nSimulator status:")

for device_id in devices:
    status = "RUNNING" if running[device_id] else "STOPPED"
    print(f"  {device_id}: {status}")

print()


def main():
print("Registering devices...\n")

for device_id, name in devices.items():
    register_device(device_id, name)

print("\nStarting heartbeat simulator...")
print("Heartbeat interval: 5 seconds")
print()
print("Commands:")
print("  stop device-01")
print("  start device-01")
print("  status")
print("  exit")
print()

for device_id in devices:
    thread = threading.Thread(
        target=simulate_device,
        args=(device_id,),
        daemon=True
    )
    thread.start()

try:
    while True:
        command = input("> ").strip().lower()

        if command == "exit":
            print("Simulator stopped.")
            break

        elif command == "status":
            show_status()

        elif command.startswith("stop "):
            device_id = command.split(" ", 1)[1]

            if device_id in running:
                running[device_id] = False
                print(f"{device_id} stopped.")
            else:
                print(f"Unknown device: {device_id}")

        elif command.startswith("start "):
            device_id = command.split(" ", 1)[1]

            if device_id in running:
                running[device_id] = True
                print(f"{device_id} started.")
            else:
                print(f"Unknown device: {device_id}")

        else:
            print("Unknown command.")

except KeyboardInterrupt:
    print("\nSimulator stopped.")


if name == "main":
main()