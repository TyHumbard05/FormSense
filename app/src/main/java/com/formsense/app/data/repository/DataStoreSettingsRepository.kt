package com.formsense.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.formSenseDataStore by preferencesDataStore(name = "formsense_settings")

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : SettingsRepository {
    override val settings = context.formSenseDataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map { preferences ->
            AppSettings(
                defaultTargetReps = preferences[DefaultTargetReps]
                    ?.coerceIn(MIN_TARGET_REPS, MAX_TARGET_REPS)
                    ?: DEFAULT_TARGET_REPS,
                hapticFeedbackEnabled = preferences[HapticFeedbackEnabled] ?: true,
                keepScreenAwake = preferences[KeepScreenAwake] ?: true,
            )
        }

    override suspend fun setDefaultTargetReps(value: Int) {
        context.formSenseDataStore.edit { preferences ->
            preferences[DefaultTargetReps] = value.coerceIn(MIN_TARGET_REPS, MAX_TARGET_REPS)
        }
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        context.formSenseDataStore.edit { preferences ->
            preferences[HapticFeedbackEnabled] = enabled
        }
    }

    override suspend fun setKeepScreenAwake(enabled: Boolean) {
        context.formSenseDataStore.edit { preferences ->
            preferences[KeepScreenAwake] = enabled
        }
    }

    private companion object {
        val DefaultTargetReps = intPreferencesKey("default_target_reps")
        val HapticFeedbackEnabled = booleanPreferencesKey("haptic_feedback_enabled")
        val KeepScreenAwake = booleanPreferencesKey("keep_screen_awake")

        const val DEFAULT_TARGET_REPS = 12
        const val MIN_TARGET_REPS = 1
        const val MAX_TARGET_REPS = 50
    }
}
