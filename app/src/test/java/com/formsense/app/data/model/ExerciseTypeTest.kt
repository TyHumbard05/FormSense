package com.formsense.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseTypeTest {
    @Test
    fun routeNamesResolveToExpectedExercises() {
        assertEquals(ExerciseType.Squat, ExerciseType.fromRoute("squat"))
        assertEquals(ExerciseType.BicepCurl, ExerciseType.fromRoute("bicep-curl"))
        assertEquals(ExerciseType.ShoulderRaise, ExerciseType.fromRoute("shoulder-raise"))
    }

    @Test
    fun unknownRouteFallsBackToSquat() {
        assertEquals(ExerciseType.Squat, ExerciseType.fromRoute("unknown"))
        assertEquals(ExerciseType.Squat, ExerciseType.fromRoute(null))
    }
}
