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

## Git Workflow

- `main` — stable/demo-ready code
- `dev` — shared development/integration branch
- `feature/<name>` — individual feature branches

Create feature branches from `dev`, push them to GitHub, and merge them back into `dev` with pull requests. Merge `dev` into `main` when the team has a stable milestone.

See [CONTRIBUTING.md](CONTRIBUTING.md) for the team workflow.

## Status

**Active development — Fall 2026 Missouri S&T capstone project.**

## Disclaimer

FormSense is an academic proof of concept and is **not intended to provide medical diagnosis or replace professional medical or physical-therapy guidance**.
