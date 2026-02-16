# GurionRock Pro Max Ultra Over 9000 — Perception & Mapping System
*Java Concurrency • Microservices • Synchronization • SLAM Simulation*

---

##  Overview
This project simulates the perception and mapping system of a robotic vacuum — the **GurionRock Pro Max Ultra Over 9000** — as part of the *SPL225 (Software Project Lab)* course at Ben-Gurion University.

The system integrates simulated sensors (Camera, LiDAR, GPS/IMU) using a **custom Java microservices framework** that supports asynchronous message passing, event handling, and concurrent processing.  
The architecture is inspired by **ROS (Robot Operating System)** and implements a simplified **Fusion-SLAM** algorithm.

---

##  Main Components

### 1. `Future` Class
Implements a custom asynchronous future mechanism:
- `get()`, `get(timeout, TimeUnit)` – waits for a result  
- `resolve()` – sets result  
- `isDone()` – checks completion status  

### 2. Microservices Framework
Implements a multithreaded **MessageBus** singleton for inter-service communication.  
Supports:
- **Events** (point-to-point) and **Broadcasts** (to all subscribers)  
- **Round-robin** event dispatch  
- Thread-safe registration, subscription, and message delivery  

Key classes:
- `MessageBusImpl`
- `MicroService` (abstract base class)
- `Event`, `Broadcast`, `Message`

### 3. GurionRock Perception-Mapping System
Built on top of the framework, this simulation connects:
- **CameraService** → detects objects and sends `DetectObjectsEvent`
- **LiDarWorkerService** → processes detections into `TrackedObjectsEvent`
- **PoseService** → provides robot pose via `PoseEvent`
- **FusionSlamService** → fuses LiDAR & Camera data to build global landmarks
- **TimeService** → drives system clock via `TickBroadcast`

All components are synchronized through ticks representing time progression.

---

##  Data Flow Example
```
[TimeService] → TickBroadcast
   ↓
[CameraService] → DetectObjectsEvent
   ↓
[LiDarWorkerService] → TrackedObjectsEvent
   ↓
[FusionSlamService] → Map + Statistics Update
```

---

##  Input & Output

### Input
Four JSON files:
1. **Configuration file** – defines sensors, frequencies, data paths  
2. **Pose data** – robot positions per tick  
3. **LiDAR data** – cloud points per object  
4. **Camera data** – detected objects per tick  

### Output
Single file `output_file.json` containing:
- **Statistics** – runtime, number of detections/tracks/landmarks  
- **World Map** – final landmarks with coordinates  
- **Error Report** (if crash occurs) – including faulty sensor & last known frames  

---

##  Testing
Developed using **Test-Driven Development (TDD)** with **JUnit**.  
Includes unit tests for:
- `MessageBusImpl`
- `FusionSlamService`
- `Camera` / `LiDar` data preparation logic  

---

##  Build & Run
Project is built using **Maven**.

```bash
mvn clean compile
mvn test
mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="path/to/config.json"
```

Ensure compatibility with **UNIX CS Lab Environment (Java 8)**.

---

