package com.formsense.app.domain.analyzer

import androidx.camera.core.ImageProxy
import com.formsense.app.data.model.ExerciseType
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class FakePostureAnalyzer @Inject constructor() : PostureAnalyzer {
    override fun submitFrame(frame: ImageProxy) {
        frame.close()
    }

    override fun results(exercise: ExerciseType): Flow<PostureResult> = flow {
        emit(
            PostureResult(
                timestampMillis = System.currentTimeMillis(),
                exercise = exercise,
                phase = MovementPhase.Ready,
                repCount = 0,
                formScore = 0,
                kneeAngleDegrees = null,
                hipAngleDegrees = null,
                feedback = emptyList(),
                completedRepScore = null,
                modelVersion = MODEL_VERSION,
            ),
        )
    }

    private companion object {
        const val MODEL_VERSION = "fake-analyzer-1.0"
    }
}