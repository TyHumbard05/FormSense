package com.formsense.app.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.formsense.app.data.model.RecentSession
import com.formsense.app.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ProgressUiState(
    val isLoading: Boolean = true,
    val sessionCount: Int = 0,
    val totalReps: Int = 0,
    val averageScore: Int = 0,
    val bestScore: Int = 0,
    val sessions: List<RecentSession> = emptyList(),
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    sessionRepository: SessionRepository,
) : ViewModel() {
    val uiState = sessionRepository.observeAllSessions()
        .map { sessions ->
            ProgressUiState(
                isLoading = false,
                sessionCount = sessions.size,
                totalReps = sessions.sumOf { it.completedReps },
                averageScore = sessions.takeIf { it.isNotEmpty() }
                    ?.map { it.averageScore }
                    ?.average()
                    ?.toInt()
                    ?: 0,
                bestScore = sessions.maxOfOrNull { it.averageScore } ?: 0,
                sessions = sessions,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProgressUiState(),
        )
}
