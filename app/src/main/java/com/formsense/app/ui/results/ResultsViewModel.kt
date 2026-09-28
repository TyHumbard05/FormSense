package com.formsense.app.ui.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.formsense.app.data.model.RecentSession
import com.formsense.app.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResultsUiState(
    val isLoading: Boolean = true,
    val session: RecentSession? = null,
    val recentSessions: List<RecentSession> = emptyList(),
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ResultsEvent {
    data object Deleted : ResultsEvent
}

@HiltViewModel
class ResultsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
) : ViewModel() {
    private val sessionId = savedStateHandle.get<String>("sessionId")?.toLongOrNull() ?: -1L
    private val operationState = MutableStateFlow(ResultsOperationState())

    private val _events = Channel<ResultsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val uiState = combine(
        sessionRepository.observeSession(sessionId),
        sessionRepository.observeRecentSessions(),
        operationState.asStateFlow(),
    ) { session, recent, operation ->
        ResultsUiState(
            isLoading = false,
            session = session,
            recentSessions = recent,
            isDeleting = operation.isDeleting,
            errorMessage = operation.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ResultsUiState(),
    )

    fun deleteSession() {
        if (sessionId <= 0 || operationState.value.isDeleting) return
        operationState.update { it.copy(isDeleting = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { sessionRepository.deleteSession(sessionId) }
                .onSuccess { _events.send(ResultsEvent.Deleted) }
                .onFailure { error ->
                    operationState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = error.message ?: "Unable to delete this session.",
                        )
                    }
                }
        }
    }
}

private data class ResultsOperationState(
    val isDeleting: Boolean = false,
    val errorMessage: String? = null,
)
