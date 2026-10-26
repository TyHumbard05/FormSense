package com.formsense.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.formsense.app.data.repository.AuthRepository
import com.formsense.app.data.repository.SessionRepository
import com.formsense.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userId: Long? = null,
    val userName: String = "",
    val userEmail: String = "",
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
    private val authRepository: AuthRepository,
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

        viewModelScope.launch {
            authRepository.activeUser.collect { user ->
                _uiState.update {
                    it.copy(
                        userId = user?.id,
                        userName = user?.fullName.orEmpty(),
                        userEmail = user?.email.orEmpty(),
                    )
                }
            }
        }
    }

    fun updateUserName(newName: String) {
        val currentUserId = _uiState.value.userId ?: return
        viewModelScope.launch {
            authRepository.updateUserName(currentUserId, newName)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.logout()
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
