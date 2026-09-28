package com.formsense.app.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.camera.core.ImageProxy
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
    val usesSimulatedAnalysis: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val keepScreenAwake: Boolean = true,
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
) : ViewModel() {
    private val exercise = ExerciseType.fromRoute(savedStateHandle["exercise"])
    private val startedAtMillis = System.currentTimeMillis()
    private val progressTracker = SessionProgressTracker(targetReps = DEFAULT_TARGET_REPS)
    private var modelVersion = "unknown"

    private val _uiState = MutableStateFlow(SessionUiState(exercise = exercise))
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<SessionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var analyzerJob: Job? = null

    init {
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
        postureAnalyzer.submitFrame(frame)
    }

    fun retryAnalyzer() {
        if (analyzerJob?.isActive == true || _uiState.value.isSaving) return
        _uiState.update { it.copy(errorMessage = null) }
        startAnalyzer()
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
    }
}
