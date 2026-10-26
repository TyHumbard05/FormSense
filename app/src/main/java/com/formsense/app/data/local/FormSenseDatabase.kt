package com.formsense.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.formsense.app.data.local.dao.UserDao
import com.formsense.app.data.local.dao.WorkoutSessionDao
import com.formsense.app.data.local.entity.RepetitionEntity
import com.formsense.app.data.local.entity.UserEntity
import com.formsense.app.data.local.entity.WorkoutSessionEntity

@Database(
    entities = [WorkoutSessionEntity::class, RepetitionEntity::class, UserEntity::class], // Add UserEntity
    version = 2, // Increment database version
    exportSchema = true,
)
abstract class FormSenseDatabase : RoomDatabase() {
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun userDao(): UserDao
}