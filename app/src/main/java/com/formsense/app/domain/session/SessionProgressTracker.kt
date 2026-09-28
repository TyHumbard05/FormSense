package com.formsense.app.domain.session

import com.formsense.app.domain.analyzer.PostureResult

/**
 * Collects completed repetitions independently from the UI and analyzer implementation.
 *
 * Analyzer results may be repeated or arrive out of order. Keying scores by repetition number
 * makes completion and persistence deterministic and prevents duplicate repetitions in Room.
 */
class SessionProgressTracker(
    targetReps: Int,
) {
    var targetReps: Int = targetReps.coerceAtLeast(1)
        private set

    private val scoresByRep = sortedMapOf<Int, Int>()

    val completedReps: Int
        get() = scoresByRep.size

    val isTargetReached: Boolean
        get() = completedReps >= targetReps

    fun updateTargetReps(value: Int) {
        targetReps = value.coerceAtLeast(1)
    }

    fun accept(result: PostureResult): Boolean {
        val score = result.completedRepScore ?: return false
        val repNumber = result.repCount
        if (repNumber <= 0 || repNumber > targetReps || scoresByRep.containsKey(repNumber)) {
            return false
        }

        scoresByRep[repNumber] = score.coerceIn(0, 100)
        return true
    }

    fun scores(): List<Int> = scoresByRep.values.toList()
}
