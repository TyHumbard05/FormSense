package com.formsense.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.formsense.app.data.repository.SessionRepository
import com.formsense.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val defaultTargetReps: Int = 12,
    val hapticFeedbackEnabled: Boolean = true,
    val keepScreenAwake: Boolean = true,
    val isDeleting: Boolean = false,
    val message: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update {
                    it.copy(
                        defaultTargetReps = settings.defaultTargetReps,
                        hapticFeedbackEnabled = settings.hapticFeedbackEnabled,
                        keepScreenAwake = settings.keepScreenAwake,
                    )
                }
            }
        }
    }

    fun setDefaultTargetReps(value: Int) {
        viewModelScope.launch { settingsRepository.setDefaultTargetReps(value) }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setHapticFeedbackEnabled(enabled) }
    }

    fun setKeepScreenAwake(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setKeepScreenAwake(enabled) }
    }

    fun deleteAllSessions() {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(isDeleting = true, message = null) }
        viewModelScope.launch {
            runCatching { sessionRepository.deleteAllSessions() }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            message = "All locally saved sessions were deleted.",
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            message = error.message ?: "Unable to delete saved sessions.",
                        )
                    }
                }
        }
    }
}
