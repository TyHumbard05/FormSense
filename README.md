# FormSense

**AI Exercise Form and Rehab Coach**

FormSense is a Missouri S&T capstone project exploring how computer vision can be used to analyze exercise and rehabilitation movements. The goal is to track body landmarks from a camera feed, calculate joint angles, recognize repetitions, and provide useful form feedback.

## Project Goals

The first version is focused on building a reliable proof of concept rather than a medical device. The team plans to fully optimize one exercise session first—most likely squats—before expanding to additional movements such as bicep curls and shoulder raises.

## Planned Features

- Native Android application
- Live camera input
- Pose and body-landmark tracking
- Joint-angle calculations
- Repetition counting
- Rule-based form analysis
- Real-time corrective feedback
- End-of-session summary
- Exercise history and progress tracking

## Example Squat Analysis

FormSense may evaluate factors such as:

- Squat depth
- Knee alignment
- Torso lean
- Rep completion
- Consistency between repetitions

## Planned Technology

- **Kotlin** — primary Android language
- **Jetpack Compose** — native Android user interface
- **CameraX** — live camera input
- **MediaPipe** — pose estimation and landmark tracking
- **Room / SQLite** — local session and progress storage
- **Git / GitHub** — version control and team collaboration

Additional backend or machine-learning services may be added later if the project needs them.

## Development Approach

1. Build the main Android screens and navigation.
2. Establish reliable CameraX input and pose tracking.
3. Calculate useful joint angles from detected landmarks.
4. Implement repetition counting.
5. Add rule-based feedback for squat form.
6. Save session results and show progress/history.
7. Expand to additional exercises.

A machine-learning good/bad-form classifier may be explored later if enough useful training data is available.

For the detailed, ordered frontend/backend plan, milestones, integration contract, and release checklist, see the [Application Development Roadmap](docs/APP_DEVELOPMENT_ROADMAP.md).

## Git Workflow

- `main` — stable/demo-ready code
- `dev` — shared development/integration branch
- `feature/<name>` — individual feature branches

Create feature branches from `dev`, push them to GitHub, and merge them back into `dev` with pull requests. Merge `dev` into `main` when the team has a stable milestone.

See [CONTRIBUTING.md](CONTRIBUTING.md) for the team workflow.

## Status

**Active development — Fall 2026 Missouri S&T capstone project.**

The `feature/ui-shell` branch contains the first local-first Android MVP foundation:

- Home, exercise setup, live session, results, history, progress, and settings screens
- Room-backed sessions and repetition scores that survive app restarts
- DataStore-backed target-repetition, haptic, and keep-screen-awake preferences
- CameraX preview, permission handling, and latest-frame analysis pipeline
- A scripted posture analyzer that drives reps, form scores, joint angles, and feedback
- Automatic completion at the target repetition count and persisted result navigation
- Hilt dependency injection, lifecycle-aware ViewModels, unit tests, and Android lint

## Run the App & Backend

### 1. Start the Java WebSocket Backend

Install JDK 17 or newer and JBang. From the repository root, run:

```powershell
jbang backend/App.java
```

The server listens on port `5000` at `ws://localhost:5000/api/camera_socket`.
The Android emulator connects through `ws://10.0.2.2:5000/api/camera_socket`.
For a physical Android device, change `socketUrl` in
`app/src/main/java/com/formsense/app/data/remote/WebSocketClient.kt` to your
computer's LAN IP address and allow inbound TCP port `5000` through the firewall.

You may need to setup ADB Reverse in order for the backend to communicate with your
pc from the emulator as the communicating packets will be silently dropped by your firewall.
To do this do the following:

In Android Studio's top toolbar, click the run configuration dropdown (next to the green Run triangle, usually labeled app) and select Edit Configurations....

Scroll down to the Before launch section at the bottom and click + → Run External Tool.

In the popup, click + to create a new tool and fill in:

Name: ADB Reverse 5000

Program: C:\Users\<YourUsername>\AppData\Local\Android\Sdk\platform-tools\adb.exe (click the folder icon to browse straight to AppData\Local\Android\Sdk\platform-tools\adb.exe)

Arguments: reverse tcp:5000 tcp:5000

Click OK → OK → Apply.

### 2. Run the Android App

1. Open the repository root in Android Studio.
2. Allow Gradle to sync with JDK 21 and the installed Android SDK.
3. Select a Pixel emulator running API 24 or newer.
4. Start the Java backend, then run the `app` configuration.

### WebSocket message protocol

- Android sends JSON text messages for `session_start`, `session_end`, and camera-frame metadata.
- Android sends JPEG camera frames as WebSocket binary messages (up to 5 frames/second).
- Java acknowledges text messages with `message_received` JSON.
- Java decodes each JPEG, logs its dimensions, and responds with `camera_frame_received` JSON.
- The Android session screen displays the latest backend response.

This is a transport/integration baseline: it verifies that real CameraX frames and two-way messages reach the Java backend. It does not yet run pose estimation or return real exercise metrics; connect the Java frame handler to your team's pose-analysis implementation when ready. Camera frames are streamed in memory and are not written to disk by this backend.

The current Android posture analyzer remains deterministic and simulated. It receives CameraX frames but closes them without inspecting pixels, so the full app flow can be developed and tested before the production posture model is ready.

## Disclaimer

FormSense is an academic proof of concept and is **not intended to provide medical diagnosis or replace professional medical or physical-therapy guidance**.
