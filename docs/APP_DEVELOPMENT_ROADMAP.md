# FormSense Application Development Roadmap

Last updated: September 26, 2026

## Purpose

This document is the working plan for turning the current FormSense UI prototype into a complete Android application. It covers the Android frontend, local data/backend, optional cloud services, camera integration, testing, privacy, and the integration boundary with the posture-analysis system.

The posture-analysis model itself is owned by the AI team. The Android app should communicate with it through a stable interface so both teams can work independently.

## Product Goal

The first complete release should support one exercise, preferably squats, extremely well:

1. The user opens FormSense.
2. The user selects an exercise and target repetition count.
3. The app explains camera placement and requests camera permission.
4. The user starts a session.
5. The app displays the camera feed, repetition count, joint metrics, and actionable feedback.
6. The user ends or completes the session.
7. The app saves the session and displays real results.
8. The session appears in history and contributes to progress statistics.

Build this complete vertical path before adding many exercises or cloud features.

## Current Project Status

### Already implemented

- Single-activity Android application using Jetpack Compose.
- Navigation for Home, Exercise Selection, Session, Results, Sessions, Progress, and Profile.
- Reusable cards, buttons, navigation bar, colors, typography, and theme.
- Hilt dependency injection, lifecycle-aware ViewModels, StateFlow UI state, and DataStore settings.
- Room entities, DAO, transactional session repository, exported schema, and cascade deletion.
- Persisted Home activity, session history, results by session ID, progress summaries, and settings.
- CameraX preview, permission request, latest-frame backpressure, and lifecycle-aware release.
- Stable `PostureAnalyzer` boundary plus a deterministic fake analyzer for end-to-end development.
- Session repetition aggregation, automatic target completion, retryable analyzer errors, and independent elapsed time.
- Android Studio preview for the Home screen.
- Unit tests cover route conversion, completed-session calculations, fake analysis, and repetition aggregation.
- `testDebugUnitTest` and `lintDebug` pass with JDK 21.

### Still incomplete

- The production posture model is not integrated; the fake analyzer currently ignores image pixels.
- String-based navigation should still be replaced with serializable route types.
- Camera denied-permanently and initialization-error recovery need stronger UX and tests.
- Session pause/resume and interrupted-session recovery are not implemented.
- History filtering and date filters are not implemented.
- Progress has summary metrics and recent-score bars but not weekly or per-exercise charts.
- User-visible strings still need complete migration into Android resources.
- Database migration, repository, Compose, and end-to-end test coverage remain incomplete.
- Authentication and cloud synchronization remain intentionally out of MVP scope.

## Recommended Architecture

Use a local-first, layered architecture with unidirectional data flow:

```text
CameraX
   | camera frames
   v
PostureAnalyzer interface <--- AI team's implementation
   | analysis results
   v
SessionEngine
   |-- live state ------> SessionViewModel ------> Compose UI
   `-- completed data --> SessionRepository -----> Room database
                                                    |
                       Home / History / Progress <--'
```

Recommended package structure:

```text
com.formsense.app/
  camera/
    CameraController
    FrameProcessor

  data/
    local/
      database/
      dao/
      entity/
    mapper/
    preferences/
    repository/

  domain/
    analyzer/
    model/
    session/
    usecase/

  ui/
    home/
    exercise/
    session/
    results/
    history/
    progress/
    profile/
    components/
    theme/

  navigation/
  di/
```

Keep the project in one Gradle application module initially. Split it into multiple modules only if build time or team ownership becomes difficult.

## Phase 1: Confirm Scope and the AI Contract

### Product decisions

- [ ] Select the first fully supported exercise.
- [ ] Define the exact start-to-results user journey.
- [ ] Decide whether the MVP is local-only or requires accounts.
- [ ] Decide whether analysis runs on-device or through a server.
- [ ] Define what information is stored after a session.
- [ ] Decide whether video recording is excluded from the MVP.

### Analyzer boundary

Create an interface owned by the app team and implemented by the AI team:

```kotlin
interface PostureAnalyzer {
    val status: StateFlow<AnalyzerStatus>

    suspend fun start(exercise: ExerciseType)

    suspend fun analyze(frame: AnalysisFrame): PostureResult

    suspend fun stop()
}
```

Suggested result model:

```kotlin
data class PostureResult(
    val timestampMillis: Long,
    val exercise: ExerciseType,
    val phase: MovementPhase,
    val repCount: Int,
    val confidence: Float,
    val formScore: Int,
    val jointAngles: Map<JointType, Float>,
    val feedback: List<FormFeedback>,
    val modelVersion: String,
)
```

The teams must agree on:

- [ ] Image format and input resolution.
- [ ] Rotation and front-camera mirroring.
- [ ] Pixel or normalized landmark coordinates.
- [ ] Expected analysis frequency.
- [ ] Confidence thresholds.
- [ ] Whether the AI or app owns repetition counting.
- [ ] Stable feedback codes and severity levels.
- [ ] Model loading, unavailable, and error states.
- [ ] Model version reporting.
- [ ] Behavior when no person or multiple people are visible.

Create a `FakePostureAnalyzer` that emits scripted results. The Android team should build the complete application against the fake before the real model is ready.

### Phase completion criteria

- [ ] The input and output models are documented.
- [ ] Both teams approve the interface.
- [ ] The fake analyzer can simulate a complete squat session.

## Phase 2: Build the Application Foundation

Add and configure:

- [ ] ViewModel support.
- [ ] Kotlin coroutines and StateFlow.
- [ ] Lifecycle-aware state collection.
- [ ] Room for persistent application data.
- [ ] DataStore for user preferences.
- [ ] Hilt for dependency injection.
- [ ] CameraX dependencies.
- [ ] Kotlin serialization for type-safe navigation.
- [ ] A Gradle version catalog.

Project cleanup:

- [ ] Move user-visible text into `strings.xml`.
- [ ] Create debug and release configurations.
- [ ] Add code formatting conventions.
- [ ] Add CI for `assembleDebug`, unit tests, and lint.
- [ ] Document the required JDK and Android SDK versions.
- [ ] Keep release minification disabled until the major integrations are stable.

Replace string routes with serializable route types:

```kotlin
@Serializable
data object HomeRoute

@Serializable
data class SessionRoute(val exerciseId: String)

@Serializable
data class ResultsRoute(val sessionId: String)
```

### Phase completion criteria

- [ ] Dependencies are provided through Hilt.
- [ ] Navigation arguments are type-safe.
- [ ] CI validates every pull request.
- [ ] A new developer can build the project from the README.

## Phase 3: Convert Screens to Data-Driven UI

Every destination should have a route composable that owns the ViewModel and a screen composable that only renders state:

```kotlin
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (Destination) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
    )
}
```

Example screen state:

```kotlin
data class HomeUiState(
    val isLoading: Boolean = true,
    val exercises: List<ExerciseSummary> = emptyList(),
    val recentSessions: List<SessionSummary> = emptyList(),
    val errorMessage: String? = null,
)
```

Convert screens in this order:

1. [ ] Home
2. [ ] Exercise Selection
3. [ ] Session
4. [ ] Results
5. [ ] Sessions History
6. [ ] Session Details
7. [ ] Progress
8. [ ] Settings/Profile

Every screen must support:

- [ ] Loading state.
- [ ] Populated state.
- [ ] Empty state.
- [ ] Recoverable error state.
- [ ] Android Studio previews for important states.
- [ ] Accessibility labels.
- [ ] Large-font behavior.

### Phase completion criteria

- [ ] Runtime composables no longer read from `SampleData`.
- [ ] Runtime composables contain no fake session measurements.
- [ ] ViewModels expose immutable `StateFlow` UI state.
- [ ] Preview data remains available without entering production repositories.

## Phase 4: Create the Local Data Backend

The application should work offline and without an account.

### Suggested session record

```text
id
exerciseId
startedAt
endedAt
durationMillis
targetReps
completedReps
averageFormScore
minimumFormScore
modelVersion
status
notes
```

### Suggested repetition record

```text
id
sessionId
repNumber
startedAt
endedAt
score
minimumKneeAngle
minimumHipAngle
flags
```

### Suggested feedback record

```text
id
sessionId
repId
timestamp
feedbackCode
severity
observedValue
expectedMinimum
expectedMaximum
```

### Implementation work

- [ ] Create session, repetition, and feedback entities.
- [ ] Create DAO queries returning `Flow` where appropriate.
- [ ] Add entity-to-domain-model mappers.
- [ ] Implement `SessionRepository`.
- [ ] Implement `ExerciseRepository`.
- [ ] Implement `SettingsRepository` backed by DataStore.
- [ ] Add database migration tests.
- [ ] Add fake repositories for UI tests and previews.
- [ ] Define interrupted-session recovery behavior.

Room should be the single source of truth. Home, Results, Sessions, and Progress should all use the same stored records.

Do not store raw camera frames or workout video by default. Persist meaningful calculated metrics instead.

### Phase completion criteria

- [ ] A completed session survives an app restart.
- [ ] Saved sessions appear on Home and in History.
- [ ] Progress calculations use stored session data.
- [ ] Database migrations have automated tests.

## Phase 5: Finish the Non-Camera Product

This phase can happen while the AI model is being developed.

### Home

- [ ] Display real recent sessions.
- [ ] Display only enabled exercises.
- [ ] Add a useful first-use empty state.
- [ ] Optionally offer recovery for an interrupted session.

### Exercise setup

- [ ] Exercise description and instructions.
- [ ] Phone-positioning guidance.
- [ ] Target repetitions.
- [ ] Camera permission status.
- [ ] Safety notice.
- [ ] Start-session action.

### Sessions history

- [ ] Chronological list.
- [ ] Exercise filter.
- [ ] Date filter.
- [ ] Session-detail navigation.
- [ ] Empty state.
- [ ] Delete confirmation.

### Results

- [ ] Navigate with `sessionId` rather than only an exercise type.
- [ ] Load the saved session through the repository.
- [ ] Show completed reps, duration, and score.
- [ ] Show per-repetition scores.
- [ ] Show strengths and improvement areas.
- [ ] Add Try Again and Return Home actions.

### Progress

- [ ] Sessions completed per week.
- [ ] Average score by exercise.
- [ ] Total completed repetitions.
- [ ] Score trend.
- [ ] Common feedback categories.
- [ ] Personal best.

### Profile and settings

- [ ] Display name.
- [ ] Units.
- [ ] Default target repetitions.
- [ ] Audio and haptic feedback preferences.
- [ ] Keep-screen-awake preference.
- [ ] Privacy information.
- [ ] Export data.
- [ ] Delete all local data.
- [ ] App and model versions.

### Phase completion criteria

- [ ] All bottom-navigation destinations are functional.
- [ ] All information comes from screen state or repositories.
- [ ] No screen depends on the camera or real AI to be testable.

## Phase 6: Integrate CameraX

Replace the visual camera placeholder with:

- [ ] Camera permission declaration.
- [ ] Permission explanation before the system prompt.
- [ ] Denied and permanently-denied states.
- [ ] Camera preview.
- [ ] Image analysis use case.
- [ ] Rotation correction.
- [ ] Front-camera mirror correction when applicable.
- [ ] Latest-frame backpressure so frames do not accumulate.
- [ ] Lifecycle-aware start, pause, resume, and release.
- [ ] Camera unavailable and initialization-error handling.
- [ ] Physical-device testing.

Initially send frames to `FakePostureAnalyzer`. Do not wait for the production AI implementation to test the camera pipeline.

### Phase completion criteria

- [ ] Preview is stable on physical devices.
- [ ] Frames reach the fake analyzer.
- [ ] Leaving the session releases the camera.
- [ ] UI remains responsive during analysis.
- [ ] Memory usage remains stable during a long session.

## Phase 7: Implement the Live Session Engine

The session lifecycle should be owned by a testable state holder, not by Compose code.

```text
Idle
  -> PreparingCamera
  -> LoadingAnalyzer
  -> Ready
  -> Running
       -> Paused
       -> Finishing
            -> Completed
       -> Error
```

Suggested state:

```kotlin
data class SessionUiState(
    val status: SessionStatus,
    val exercise: Exercise,
    val elapsedTime: Duration,
    val repCount: Int,
    val targetReps: Int,
    val formScore: Int?,
    val jointAngles: Map<JointType, Float>,
    val liveFeedback: List<FormFeedback>,
    val cameraState: CameraState,
    val analyzerState: AnalyzerStatus,
)
```

The session engine must:

- [ ] Prevent duplicate starts.
- [ ] Start and stop the analyzer safely.
- [ ] Track elapsed time.
- [ ] Convert feedback codes into localized messages.
- [ ] Debounce rapidly changing feedback.
- [ ] Avoid repeatedly flashing the same warning.
- [ ] Finalize repetitions consistently.
- [ ] Aggregate repetition and session scores.
- [ ] Recover or safely discard interrupted sessions.
- [ ] Save a completed session in one transaction.
- [ ] Navigate to Results only after the save succeeds.

### Phase completion criteria

- [ ] A scripted fake-analysis session completes from start to results.
- [ ] The completed session is persisted correctly.
- [ ] History and progress update automatically.

## Phase 8: Integrate the Real Analyzer

Swap the fake implementation for the AI team's implementation through dependency injection.

Integration order:

1. [ ] Feed recorded test frames into the analyzer.
2. [ ] Verify stable result models and feedback codes.
3. [ ] Connect analyzer results to the session engine.
4. [ ] Verify UI state updates.
5. [ ] Verify saved session calculations.
6. [ ] Test the complete flow on physical devices.

Measure:

- [ ] Model startup time.
- [ ] Frames analyzed per second.
- [ ] Feedback latency.
- [ ] CPU and memory use.
- [ ] Battery use and device temperature.
- [ ] Dropped-frame rate.
- [ ] Oldest supported-device performance.
- [ ] Low-light behavior.
- [ ] No-person and multiple-person behavior.
- [ ] Portrait and landscape behavior.

The AI implementation must not directly navigate, update Compose state, or write to Room.

### Phase completion criteria

- [ ] Switching between fake and real analyzers only changes a dependency binding.
- [ ] Real-time analysis does not block the main thread.
- [ ] Performance meets the team's agreed device targets.

## Phase 9: Decide on Cloud Services

Cloud services are optional for the first complete local release.

Add a cloud backend only if the product needs:

- User accounts.
- Cross-device history.
- Backup and restore.
- Coach or therapist dashboards.
- Sharing.
- Remote configuration.
- Server-side analysis.

If synchronization is added:

- [ ] Keep Room as the UI's local source of truth.
- [ ] Add remote IDs, update timestamps, and sync status.
- [ ] Write locally first.
- [ ] Queue synchronization through WorkManager.
- [ ] Support retries and offline use.
- [ ] Define conflict-resolution rules.
- [ ] Never block a workout because the network is unavailable.
- [ ] Secure authentication tokens.
- [ ] Use HTTPS for every request.
- [ ] Test backend security rules before distribution.

Firebase is a practical option for a small Android team that needs Authentication, Firestore, Storage, Functions, and crash reporting. A custom API can be used if the team needs greater control. Make this decision after the local app works end to end.

## Phase 10: Privacy, Security, and Safety

- [ ] Request camera permission only when beginning camera-related work.
- [ ] Clearly explain what the camera analyzes.
- [ ] Do not store frames or video by default.
- [ ] Do not transmit frames without clear user disclosure and consent.
- [ ] Store private data in internal application storage.
- [ ] Never log frames, landmarks, or personal information in production.
- [ ] Use HTTPS for all network traffic.
- [ ] Add deletion controls.
- [ ] Define data-retention rules.
- [ ] Create a privacy policy.
- [ ] Keep the non-medical-device disclaimer visible.
- [ ] Avoid medical diagnoses in feedback text.
- [ ] Prefer language such as "Try keeping your knees aligned."

## Phase 11: Testing

### Unit tests

- [ ] Score aggregation.
- [ ] Repetition finalization.
- [ ] Session state transitions.
- [ ] Feedback debouncing.
- [ ] Route serialization.
- [ ] Entity/domain mapping.
- [ ] Progress calculations.
- [ ] Error recovery.
- [ ] Fake analyzer scenarios.

### Repository and database tests

- [ ] Insert and load sessions.
- [ ] Sessions with multiple repetitions.
- [ ] Cascade-delete behavior.
- [ ] Migration tests.
- [ ] History sorting and filtering.
- [ ] Progress queries.
- [ ] Interrupted-session recovery.

### Compose tests

- [ ] Home loading, empty, populated, and error states.
- [ ] Exercise selection.
- [ ] Camera permission states.
- [ ] Live feedback changes.
- [ ] Ending a session.
- [ ] Results rendering.
- [ ] History navigation.
- [ ] Accessibility semantics.

### End-to-end tests

- [ ] Fake analyzer to session engine to database to results.
- [ ] Process recreation during a session.
- [ ] Backgrounding and reopening the app.
- [ ] Camera permission revocation.
- [ ] Analyzer initialization failure.
- [ ] Database failure.
- [ ] Offline and online synchronization if cloud services are added.

### Physical-device matrix

- [ ] Oldest supported Android version.
- [ ] Current Android version.
- [ ] Lower-performance device.
- [ ] Reference or Pixel device.
- [ ] Portrait and landscape.
- [ ] Large font.
- [ ] Dark mode.
- [ ] TalkBack.
- [ ] Camera denied.
- [ ] No internet.
- [ ] Long session and thermal throttling.

## Phase 12: UX and Accessibility

- [ ] Adapt exercise cards to narrow and wide screens.
- [ ] Test tablets, foldables, split screen, and rotation.
- [ ] Support large fonts without clipped content.
- [ ] Maintain minimum touch-target sizes.
- [ ] Add meaningful content descriptions.
- [ ] Verify color contrast.
- [ ] Never communicate feedback through color alone.
- [ ] Make sound and haptics optional.
- [ ] Announce important feedback for screen-reader users.
- [ ] Provide understandable errors and retry actions.
- [ ] Keep the session controls usable while exercising at a distance.

## Phase 13: Performance and Release Preparation

- [ ] Profile session startup.
- [ ] Profile frame-analysis latency.
- [ ] Keep all analysis off the main thread.
- [ ] Throttle visible UI updates independently from analyzer frequency.
- [ ] Avoid writing every analyzed frame to the database.
- [ ] Save only repetitions and meaningful feedback events.
- [ ] Run lint, unit tests, UI tests, and release builds in CI.
- [ ] Test the signed release build.
- [ ] Enable minification and verify model/native dependencies.
- [ ] Add non-sensitive crash and performance monitoring.
- [ ] Prepare app icons, screenshots, store copy, and privacy disclosures.
- [ ] Run pilot usability sessions.
- [ ] Freeze features before the final demonstration.

## Suggested 16-Week Schedule

| Weeks | Primary deliverable |
| --- | --- |
| 1-2 | Requirements, one-exercise MVP, analyzer interface, and fake analyzer |
| 3-4 | ViewModels, repositories, Hilt, type-safe navigation, and CI |
| 5-6 | Room database, DataStore, data-driven Home, and real Results structure |
| 7-8 | History, session details, Progress, and Settings |
| 9-10 | CameraX preview, permissions, and frame pipeline |
| 11-12 | Session engine and complete fake-analyzer workflow |
| 13 | Real analyzer integration |
| 14 | Performance profiling and physical-device testing |
| 15 | Accessibility, privacy, and release configuration |
| 16 | Pilot testing, demo rehearsal, and release candidate |

## Parallel Team Work

While the AI team develops the model:

- App developer A can build Room, DAOs, and repositories.
- App developer B can convert screens to ViewModel-driven state.
- App developer C can build CameraX and the fake analyzer pipeline.
- The AI and app teams should jointly maintain the analyzer contract and recorded test inputs.

The real analyzer should be integrated only after the fake analyzer can drive the complete application.

## Scope Priorities

### Must have

- One fully supported exercise.
- Camera preview.
- Real-time repetition count and feedback.
- Reliable session lifecycle.
- Results based on the completed session.
- Persistent history.
- Basic progress information.
- Permission, error, privacy, and safety handling.
- Automated tests.

### Should have

- Multiple exercises.
- Progress filtering.
- Audio and haptic cues.
- Data export.
- Settings and personalization.
- Adaptive tablet UI.

### Later

- Accounts and cloud synchronization.
- Coach or therapist portal.
- Sharing.
- Video recording.
- Social features.
- Remote model delivery.
- Notifications.

## Immediate Backlog

Complete these items in order:

1. [x] Document the analyzer input/output contract in code and the roadmap.
2. [x] Implement `PostureAnalyzer` and `FakePostureAnalyzer`.
3. [x] Add Hilt, ViewModel, coroutines, Room, and DataStore.
4. [x] Create Room entities, DAOs, and `SessionRepository`.
5. [x] Create `HomeUiState` and `HomeViewModel`.
6. [x] Remove runtime sample-session usage from Home.
7. [x] Create `SessionUiState` and deterministic repetition aggregation.
8. [x] Convert Results to load a saved `sessionId`.
9. [x] Implement History and Session Details.
10. [x] Add CameraX preview and permission handling.
11. [x] Feed CameraX frames to the fake analyzer boundary.
12. [x] Complete and persist a fake-analyzed session.
13. [ ] Integrate the real analyzer.
14. [ ] Add progress queries and charts.
15. [ ] Harden testing, privacy, accessibility, and release behavior.

## Release Definition of Done

FormSense is ready for a complete MVP demonstration when:

- [ ] A user can finish the full journey without sample data.
- [ ] The app supports one exercise reliably on a physical device.
- [ ] Camera and analyzer resources follow the Android lifecycle.
- [ ] Sessions survive app restarts.
- [ ] Results, History, Home, and Progress agree on the same data.
- [ ] The app works without an internet connection.
- [ ] No raw video is retained unless the user explicitly enables it.
- [ ] Permission denials and operational errors are recoverable.
- [ ] Core business rules have unit tests.
- [ ] Database migrations are tested.
- [ ] The signed release build passes the team's device matrix.
- [ ] Privacy and non-medical-use disclosures are complete.

## Reference Documentation

- [Android app architecture](https://developer.android.com/topic/architecture)
- [Android architecture recommendations](https://developer.android.com/topic/architecture/recommendations)
- [Lifecycle in Jetpack Compose](https://developer.android.com/topic/libraries/architecture/lifecycle)
- [Room persistence library](https://developer.android.com/jetpack/androidx/releases/room)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Type-safe Navigation Compose destinations](https://developer.android.com/guide/navigation/type-safe-destinations)
- [CameraX architecture](https://developer.android.com/media/camera/camerax/architecture)
- [Android testing strategies](https://developer.android.com/training/testing/fundamentals/strategies)
- [Android security guidance](https://developer.android.com/privacy-and-security/security-tips)
- [Adaptive Android apps](https://developer.android.com/develop/adaptive-apps)
- [WorkManager](https://developer.android.com/reference/androidx/work/WorkManager)
- [Firebase Android setup](https://firebase.google.com/docs/android/setup)
