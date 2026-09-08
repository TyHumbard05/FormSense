# FormSense

**AI Exercise Form and Rehab Coach**

FormSense is a Missouri S&T capstone project exploring how computer vision can be used to analyze exercise and rehabilitation movements. The goal is to track body landmarks from a camera feed, calculate joint angles, recognize repetitions, and provide useful form feedback.

## Project Goals

The first version is focused on building a reliable proof of concept rather than a medical device. The team plans to fully optimize one exercise session first—most likely squats—before expanding to additional movements such as bicep curls and shoulder raises.

## Planned Features

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

- **Python** — vision and analysis logic
- **OpenCV** — video processing
- **MediaPipe** — pose estimation and landmark tracking
- **FastAPI** — backend/API layer
- **React** — frontend interface
- **Database** — users, sessions, repetitions, and feedback
- **Git/GitHub** — version control and team collaboration

## My Role

**Team Lead / Systems Engineer**

I lead a four-person capstone team and help coordinate project scope, task ownership, system architecture, GitHub workflow, internal deadlines, and integration between the vision, frontend, backend, and analysis components.

## Development Approach

1. Establish reliable camera and pose tracking.
2. Calculate useful joint angles from detected landmarks.
3. Implement repetition counting.
4. Add rule-based feedback for squat form.
5. Connect the vision pipeline to the application backend and frontend.
6. Add session history and expand to additional exercises.

A machine-learning good/bad-form classifier may be explored later if enough useful training data is available.

## Status

**Active development — Fall 2026 Missouri S&T capstone project.**

This repository will be updated as implementation progresses.

## Disclaimer

FormSense is an academic proof of concept and is **not intended to provide medical diagnosis or replace professional medical or physical-therapy guidance**.
