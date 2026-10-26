package com.formsense.app.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.formsense.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val signUpError: String? = null,
    val currentUserId: Long? = null,
    val currentUserName: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun clearErrors() {
        _uiState.update { it.copy(loginError = null, signUpError = null) }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
    ) {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail.isEmpty() || cleanPassword.isEmpty()) {
            _uiState.update { it.copy(loginError = "Please enter both email and password.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, loginError = null) }

        viewModelScope.launch {
            authRepository.login(cleanEmail, cleanPassword)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = null,
                            currentUserId = user.id,
                            currentUserName = user.fullName,
                        )
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = error.message ?: "Invalid email or password.",
                        )
                    }
                }
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
    ) {
        val cleanName = name.trim()
        val cleanEmail = email.trim()

        if (cleanName.isEmpty()) {
            _uiState.update { it.copy(signUpError = "Please enter your full name.") }
            return
        }

        if (cleanEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.update { it.copy(signUpError = "Please enter a valid email address.") }
            return
        }

        if (password.length < 6) {
            _uiState.update { it.copy(signUpError = "Password must be at least 6 characters long.") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(signUpError = "Passwords do not match.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, signUpError = null) }

        viewModelScope.launch {
            authRepository.register(cleanName, cleanEmail, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            signUpError = null,
                            currentUserId = user.id,
                            currentUserName = user.fullName,
                        )
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            signUpError = error.message ?: "Failed to create account.",
                        )
                    }
                }
        }
    }
}
