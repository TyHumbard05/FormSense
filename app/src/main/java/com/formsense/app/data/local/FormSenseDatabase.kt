package com.formsense.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.formsense.app.data.local.dao.WorkoutSessionDao
import com.formsense.app.data.local.entity.RepetitionEntity
import com.formsense.app.data.local.entity.WorkoutSessionEntity

@Database(
    entities = [WorkoutSessionEntity::class, RepetitionEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class FormSenseDatabase : RoomDatabase() {
    abstract fun workoutSessionDao(): WorkoutSessionDao
}
