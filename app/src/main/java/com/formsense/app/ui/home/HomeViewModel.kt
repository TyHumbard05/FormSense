package com.formsense.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.RecentSession
import com.formsense.app.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val isLoading: Boolean = true,
    val exercises: List<ExerciseType> = ExerciseType.entries,
    val recentSessions: List<RecentSession> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    sessionRepository: SessionRepository,
) : ViewModel() {
    val uiState = sessionRepository.observeRecentSessions()
        .map { sessions ->
            HomeUiState(
                isLoading = false,
                recentSessions = sessions,
            )
        }
        .catch { error ->
            emit(
                HomeUiState(
                    isLoading = false,
                    errorMessage = error.message ?: "Unable to load recent sessions.",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )
}
