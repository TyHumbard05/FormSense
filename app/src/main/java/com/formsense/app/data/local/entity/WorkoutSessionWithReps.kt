package com.formsense.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutSessionWithReps(
    @Embedded
    val session: WorkoutSessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId",
    )
    val repetitions: List<RepetitionEntity>,
)
