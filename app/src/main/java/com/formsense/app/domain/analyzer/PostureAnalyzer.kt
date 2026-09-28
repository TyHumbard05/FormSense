package com.formsense.app.domain.analyzer

import androidx.camera.core.ImageProxy
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.FormFeedback
import kotlinx.coroutines.flow.Flow

enum class MovementPhase {
    Ready,
    Descending,
    Bottom,
    Ascending,
    Complete,
}

data class PostureResult(
    val timestampMillis: Long,
    val exercise: ExerciseType,
    val phase: MovementPhase,
    val repCount: Int,
    val formScore: Int,
    val kneeAngleDegrees: Int?,
    val hipAngleDegrees: Int?,
    val feedback: List<FormFeedback>,
    val completedRepScore: Int? = null,
    val modelVersion: String,
)

/**
 * Boundary between the Android session experience and the posture-analysis implementation.
 *
 * The fake implementation drives the app today. The AI implementation can replace it without
 * changing ViewModels or composables as long as it emits the same domain results.
 */
interface PostureAnalyzer {
    fun results(exercise: ExerciseType): Flow<PostureResult>

    /**
     * Receives the newest available CameraX frame. Implementations own the frame and must close it.
     */
    fun submitFrame(frame: ImageProxy)
}
