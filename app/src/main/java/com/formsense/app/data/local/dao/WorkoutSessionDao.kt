package com.formsense.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.formsense.app.data.local.entity.RepetitionEntity
import com.formsense.app.data.local.entity.WorkoutSessionEntity
import com.formsense.app.data.local.entity.WorkoutSessionWithReps
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY startedAtMillis DESC LIMIT :limit")
    fun observeRecentForUser(userId: Long, limit: Int): Flow<List<WorkoutSessionWithReps>>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE userId = :userId ORDER BY startedAtMillis DESC")
    fun observeAllForUser(userId: Long): Flow<List<WorkoutSessionWithReps>>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun observeById(sessionId: Long): Flow<WorkoutSessionWithReps?>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getEntityById(sessionId: Long): WorkoutSessionEntity?

    @Insert
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Insert
    suspend fun insertRepetitions(repetitions: List<RepetitionEntity>)

    @Delete
    suspend fun deleteSession(session: WorkoutSessionEntity)

    @Query("DELETE FROM workout_sessions WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Long)

    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAll()
}
