package com.formsense.app.domain.session

import com.formsense.app.data.model.ExerciseType
import com.formsense.app.domain.analyzer.MovementPhase
import com.formsense.app.domain.analyzer.PostureResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionProgressTrackerTest {
    @Test
    fun duplicateAndNonCompletedResultsAreIgnored() {
        val tracker = SessionProgressTracker(targetReps = 3)

        assertFalse(tracker.accept(result(repCount = 1, completedScore = null)))
        assertTrue(tracker.accept(result(repCount = 1, completedScore = 87)))
        assertFalse(tracker.accept(result(repCount = 1, completedScore = 92)))

        assertEquals(1, tracker.completedReps)
        assertEquals(listOf(87), tracker.scores())
        assertFalse(tracker.isTargetReached)
    }

    @Test
    fun scoresAreClampedOrderedAndStopAtTarget() {
        val tracker = SessionProgressTracker(targetReps = 3)

        assertTrue(tracker.accept(result(repCount = 2, completedScore = 110)))
        assertTrue(tracker.accept(result(repCount = 1, completedScore = -4)))
        assertTrue(tracker.accept(result(repCount = 3, completedScore = 91)))
        assertFalse(tracker.accept(result(repCount = 4, completedScore = 80)))

        assertEquals(listOf(0, 100, 91), tracker.scores())
        assertTrue(tracker.isTargetReached)
    }

    @Test
    fun updatedTargetIsEnforced() {
        val tracker = SessionProgressTracker(targetReps = 12)
        tracker.updateTargetReps(2)

        tracker.accept(result(repCount = 1, completedScore = 80))
        tracker.accept(result(repCount = 2, completedScore = 90))

        assertEquals(2, tracker.targetReps)
        assertTrue(tracker.isTargetReached)
    }

    private fun result(repCount: Int, completedScore: Int?) = PostureResult(
        timestampMillis = 1_000L,
        exercise = ExerciseType.Squat,
        phase = MovementPhase.Complete,
        repCount = repCount,
        formScore = completedScore ?: 0,
        kneeAngleDegrees = 90,
        hipAngleDegrees = 85,
        feedback = emptyList(),
        completedRepScore = completedScore,
        modelVersion = "test",
    )
}
