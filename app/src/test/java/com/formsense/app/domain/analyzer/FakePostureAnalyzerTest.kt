package com.formsense.app.domain.analyzer

import com.formsense.app.data.model.ExerciseType
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FakePostureAnalyzerTest {
    @Test
    fun scriptedAnalysisStartsReadyAndCompletesARep() = runTest {
        val results = FakePostureAnalyzer()
            .results(ExerciseType.Squat)
            .take(3)
            .toList()

        assertEquals(MovementPhase.Ready, results.first().phase)
        assertEquals(0, results.first().repCount)
        assertEquals(1, results.last().repCount)
        assertNotNull(results.last().completedRepScore)
        assertEquals("fake-analyzer-1.0", results.last().modelVersion)
    }
}
