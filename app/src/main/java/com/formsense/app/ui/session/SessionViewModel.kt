package com.formsense.app.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.camera.core.ImageProxy
import android.os.SystemClock
import android.util.Log
import com.formsense.app.data.remote.CameraFrameEncoder
import com.formsense.app.data.remote.WebSocketClient
import com.formsense.app.data.model.CompletedSessionDraft
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.FormFeedback
import com.formsense.app.data.repository.SessionRepository
import com.formsense.app.data.repository.SettingsRepository
import com.formsense.app.domain.analyzer.MovementPhase
import com.formsense.app.domain.analyzer.PostureAnalyzer
import com.formsense.app.domain.session.SessionProgressTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionUiState(
    val exercise: ExerciseType,
    val targetReps: Int = 12,
    val repCount: Int = 0,
    val formScore: Int? = null,
    val kneeAngleDegrees: Int? = null,
    val hipAngleDegrees: Int? = null,
    val feedback: List<FormFeedback> = emptyList(),
    val phase: MovementPhase = MovementPhase.Ready,
    val elapsedSeconds: Long = 0,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val usesSimulatedAnalysis: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val keepScreenAwake: Boolean = true,
    val isWebSocketConnected: Boolean = false,
    val lastBackendMessage: String? = null,
)

sealed interface SessionEvent {
    data class Saved(val sessionId: Long) : SessionEvent
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    settingsRepository: SettingsRepository,
    private val postureAnalyzer: PostureAnalyzer,
    private val webSocketClient: WebSocketClient,
) : ViewModel() {
    private val exercise = ExerciseType.fromRoute(savedStateHandle["exercise"])
    private val startedAtMillis = System.currentTimeMillis()
    private val progressTracker = SessionProgressTracker(targetReps = DEFAULT_TARGET_REPS)
    private var modelVersion = "unknown"
    private var lastCameraFrameSentAt = 0L

    private val _uiState = MutableStateFlow(SessionUiState(exercise = exercise))
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<SessionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var analyzerJob: Job? = null

    init {
        // Connect to the Java WebSocket backend when the session starts.
        Log.d("SessionViewModel", "Connecting to Java camera socket")
        webSocketClient.connect()

        viewModelScope.launch {
            webSocketClient.isConnected.collect { connected ->
                _uiState.update { it.copy(isWebSocketConnected = connected) }
                if (connected) {
                    webSocketClient.sendMessage("""{"type":"session_start","exercise":"${exercise.name}","targetReps":${_uiState.value.targetReps}}""")
                }
            }
        }

        viewModelScope.launch {
            webSocketClient.messages.collect { message ->
                Log.d("SessionViewModel", "Socket message from server: $message")
                _uiState.update { it.copy(lastBackendMessage = message) }
            }
        }

        startAnalyzer()
        viewModelScope.launch {
            while (true) {
                delay(1_000)
                _uiState.update { current ->
                    current.copy(elapsedSeconds = elapsedSeconds())
                }
            }
        }
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                progressTracker.updateTargetReps(settings.defaultTargetReps)
                _uiState.update {
                    it.copy(
                        targetReps = settings.defaultTargetReps,
                        hapticFeedbackEnabled = settings.hapticFeedbackEnabled,
                        keepScreenAwake = settings.keepScreenAwake,
                    )
                }
            }
        }
    }

    fun finishSession() {
        if (_uiState.value.isSaving) return

        webSocketClient.sendMessage("""{"type":"session_end","exercise":"${exercise.name}","reps":${_uiState.value.repCount}}""")
        webSocketClient.disconnect()
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            analyzerJob?.cancel()
            runCatching {
                sessionRepository.saveCompletedSession(
                    CompletedSessionDraft(
                        exercise = exercise,
                        startedAtMillis = startedAtMillis,
                        endedAtMillis = System.currentTimeMillis(),
                        targetReps = _uiState.value.targetReps,
                        repScores = progressTracker.scores(),
                        modelVersion = modelVersion,
                    ),
                )
            }.onSuccess { sessionId ->
                _events.send(SessionEvent.Saved(sessionId))
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Unable to save this session.",
                    )
                }
            }
        }
    }

    fun submitCameraFrame(frame: ImageProxy) {
        // Encode before handing the ImageProxy to the analyzer, which owns/closes it.
        // Limit network traffic to five JPEG frames per second.
        val now = SystemClock.elapsedRealtime()
        if (webSocketClient.isConnected.value && now - lastCameraFrameSentAt >= CAMERA_SEND_INTERVAL_MS) {
            lastCameraFrameSentAt = now
            runCatching { CameraFrameEncoder.toJpeg(frame) }
                .onSuccess { jpeg ->
                    if (jpeg != null) {
                        val metadata = """{"type":"camera_frame_metadata","timestampNanos":${frame.imageInfo.timestamp},"rotationDegrees":${frame.imageInfo.rotationDegrees},"width":${frame.width},"height":${frame.height}}"""
                        webSocketClient.sendMessage(metadata)
                        webSocketClient.sendCameraFrame(jpeg)
                    }
                }
                .onFailure { error -> Log.w("SessionViewModel", "Camera JPEG encoding failed: ${error.message}") }
        }
        postureAnalyzer.submitFrame(frame)
    }

    fun retryAnalyzer() {
        if (analyzerJob?.isActive == true || _uiState.value.isSaving) return
        _uiState.update { it.copy(errorMessage = null) }
        startAnalyzer()
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient.disconnect()
    }

    private fun startAnalyzer() {
        analyzerJob = viewModelScope.launch {
            postureAnalyzer.results(exercise)
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Posture analysis stopped unexpectedly.")
                    }
                }
                .collect { result ->
                    progressTracker.accept(result)
                    modelVersion = result.modelVersion
                    _uiState.update { current ->
                        current.copy(
                            repCount = progressTracker.completedReps,
                            formScore = result.formScore.takeIf {
                                progressTracker.completedReps > 0
                            },
                            kneeAngleDegrees = result.kneeAngleDegrees,
                            hipAngleDegrees = result.hipAngleDegrees,
                            feedback = result.feedback,
                            phase = result.phase,
                            elapsedSeconds = elapsedSeconds(),
                        )
                    }

                    if (progressTracker.isTargetReached) {
                        finishSession()
                    }
                }
        }
    }

    private fun elapsedSeconds(): Long =
        ((System.currentTimeMillis() - startedAtMillis) / 1_000L).coerceAtLeast(0)

    private companion object {
        const val DEFAULT_TARGET_REPS = 12
        const val CAMERA_SEND_INTERVAL_MS = 200L
    }
}
