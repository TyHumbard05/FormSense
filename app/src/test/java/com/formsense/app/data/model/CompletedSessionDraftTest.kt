package com.formsense.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CompletedSessionDraftTest {
    @Test
    fun completedRepsAndAverageAreDerivedFromRepScores() {
        val draft = CompletedSessionDraft(
            exercise = ExerciseType.Squat,
            startedAtMillis = 1_000,
            endedAtMillis = 5_000,
            targetReps = 3,
            repScores = listOf(80, 90, 100),
            modelVersion = "test",
        )

        assertEquals(3, draft.completedReps)
        assertEquals(90, draft.averageScore)
    }

    @Test
    fun emptySessionHasZeroRepsAndScore() {
        val draft = CompletedSessionDraft(
            exercise = ExerciseType.Squat,
            startedAtMillis = 1_000,
            endedAtMillis = 1_000,
            targetReps = 12,
            repScores = emptyList(),
            modelVersion = "test",
        )

        assertEquals(0, draft.completedReps)
        assertEquals(0, draft.averageScore)
    }
}
