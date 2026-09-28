package com.formsense.app.data.repository

import kotlinx.coroutines.flow.Flow

data class AppSettings(
    val defaultTargetReps: Int = 12,
    val hapticFeedbackEnabled: Boolean = true,
    val keepScreenAwake: Boolean = true,
)

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setDefaultTargetReps(value: Int)

    suspend fun setHapticFeedbackEnabled(enabled: Boolean)

    suspend fun setKeepScreenAwake(enabled: Boolean)
}
