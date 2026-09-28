package com.formsense.app.domain.analyzer

import androidx.camera.core.ImageProxy
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.FeedbackSeverity
import com.formsense.app.data.model.FormFeedback
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class FakePostureAnalyzer @Inject constructor() : PostureAnalyzer {
    override fun submitFrame(frame: ImageProxy) {
        frame.close()
    }

    override fun results(exercise: ExerciseType): Flow<PostureResult> = flow {
        val scores = listOf(84, 88, 91, 79, 86, 90, 93, 87, 89, 92, 90, 94)
        var tick = 0

        emit(
            result(
                exercise = exercise,
                phase = MovementPhase.Ready,
                repCount = 0,
                score = 0,
                kneeAngle = null,
                hipAngle = null,
                feedback = listOf(
                    FormFeedback(
                        code = "ready",
                        title = "Get into position",
                        message = "Keep your full body visible and begin when ready.",
                        severity = FeedbackSeverity.Coaching,
                    ),
                ),
            ),
        )

        while (true) {
            delay(900)
            val scoreIndex = (tick / 2).coerceAtMost(scores.lastIndex)
            val completed = tick % 2 == 1
            val repCount = ((tick + 1) / 2).coerceAtMost(scores.size)
            val score = scores[scoreIndex]
            val aligned = score >= 84

            emit(
                result(
                    exercise = exercise,
                    phase = if (completed) MovementPhase.Complete else MovementPhase.Bottom,
                    repCount = repCount,
                    score = score,
                    kneeAngle = if (completed) 96 else 88,
                    hipAngle = if (completed) 91 else 82,
                    feedback = listOf(
                        if (aligned) {
                            FormFeedback(
                                code = "good_depth",
                                title = "Good depth",
                                message = "You are reaching a controlled squat depth.",
                                severity = FeedbackSeverity.Positive,
                            )
                        } else {
                            FormFeedback(
                                code = "knee_alignment",
                                title = "Watch knee alignment",
                                message = "Try keeping your knees tracking over your toes.",
                                severity = FeedbackSeverity.Coaching,
                            )
                        },
                    ),
                    completedRepScore = score.takeIf { completed },
                ),
            )
            tick++
        }
    }

    private fun result(
        exercise: ExerciseType,
        phase: MovementPhase,
        repCount: Int,
        score: Int,
        kneeAngle: Int?,
        hipAngle: Int?,
        feedback: List<FormFeedback>,
        completedRepScore: Int? = null,
    ) = PostureResult(
        timestampMillis = System.currentTimeMillis(),
        exercise = exercise,
        phase = phase,
        repCount = repCount,
        formScore = score,
        kneeAngleDegrees = kneeAngle,
        hipAngleDegrees = hipAngle,
        feedback = feedback,
        completedRepScore = completedRepScore,
        modelVersion = MODEL_VERSION,
    )

    private companion object {
        const val MODEL_VERSION = "fake-analyzer-1.0"
    }
}
