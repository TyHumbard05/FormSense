package com.formsense.app.data.model

enum class ExerciseType(
    val routeName: String,
    val displayName: String,
    val focusArea: String,
) {
    Squat("squat", "Squat", "Lower Body"),
    BicepCurl("bicep-curl", "Bicep Curl", "Upper Body"),
    ShoulderRaise("shoulder-raise", "Shoulder Raise", "Upper Body"),
    ;

    companion object {
        fun fromRoute(routeName: String?): ExerciseType =
            entries.firstOrNull { it.routeName == routeName } ?: Squat
    }
}

data class RecentSession(
    val id: Long,
    val exercise: ExerciseType,
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val targetReps: Int,
    val completedReps: Int,
    val averageScore: Int,
    val repScores: List<Int>,
    val modelVersion: String,
)

data class CompletedSessionDraft(
    val exercise: ExerciseType,
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val targetReps: Int,
    val repScores: List<Int>,
    val modelVersion: String,
) {
    val completedReps: Int = repScores.size
    val averageScore: Int = repScores.takeIf { it.isNotEmpty() }?.average()?.toInt() ?: 0
}

enum class FeedbackSeverity {
    Positive,
    Coaching,
}

data class FormFeedback(
    val code: String,
    val title: String,
    val message: String,
    val severity: FeedbackSeverity,
)
