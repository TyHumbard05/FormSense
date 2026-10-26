package com.formsense.app.data.repository

import androidx.room.withTransaction
import com.formsense.app.data.local.FormSenseDatabase
import com.formsense.app.data.local.entity.RepetitionEntity
import com.formsense.app.data.local.entity.WorkoutSessionEntity
import com.formsense.app.data.local.entity.WorkoutSessionWithReps
import com.formsense.app.data.model.CompletedSessionDraft
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.data.model.RecentSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class RoomSessionRepository @Inject constructor(
    private val database: FormSenseDatabase,
    private val settingsRepository: SettingsRepository,
) : SessionRepository {
    private val dao = database.workoutSessionDao()

    private val currentUserId: Flow<Long> = settingsRepository.settings.map { it.activeUserId ?: 0L }

    override fun observeRecentSessions(limit: Int): Flow<List<RecentSession>> =
        currentUserId.flatMapLatest { userId ->
            dao.observeRecentForUser(userId, limit)
        }.map { sessions -> sessions.map(WorkoutSessionWithReps::toDomain) }

    override fun observeAllSessions(): Flow<List<RecentSession>> =
        currentUserId.flatMapLatest { userId ->
            dao.observeAllForUser(userId)
        }.map { sessions -> sessions.map(WorkoutSessionWithReps::toDomain) }

    override fun observeSession(sessionId: Long): Flow<RecentSession?> =
        dao.observeById(sessionId).map { it?.toDomain() }

    override suspend fun saveCompletedSession(draft: CompletedSessionDraft): Long {
        val activeUserId = settingsRepository.settings.first().activeUserId ?: 0L
        return database.withTransaction {
            val sessionId = dao.insertSession(
                WorkoutSessionEntity(
                    userId = activeUserId,
                    exerciseRoute = draft.exercise.routeName,
                    startedAtMillis = draft.startedAtMillis,
                    endedAtMillis = draft.endedAtMillis,
                    targetReps = draft.targetReps,
                    completedReps = draft.completedReps,
                    averageScore = draft.averageScore,
                    modelVersion = draft.modelVersion,
                ),
            )
            val repetitions = draft.repScores.mapIndexed { index, score ->
                RepetitionEntity(
                    sessionId = sessionId,
                    repNumber = index + 1,
                    score = score.coerceIn(0, 100),
                )
            }
            if (repetitions.isNotEmpty()) {
                dao.insertRepetitions(repetitions)
            }
            sessionId
        }
    }

    override suspend fun deleteSession(sessionId: Long) {
        val session = dao.getEntityById(sessionId) ?: return
        dao.deleteSession(session)
    }

    override suspend fun deleteAllSessions() {
        val activeUserId = settingsRepository.settings.first().activeUserId ?: 0L
        dao.deleteAllForUser(activeUserId)
    }
}

private fun WorkoutSessionWithReps.toDomain(): RecentSession = RecentSession(
    id = session.id,
    exercise = ExerciseType.fromRoute(session.exerciseRoute),
    startedAtMillis = session.startedAtMillis,
    endedAtMillis = session.endedAtMillis,
    targetReps = session.targetReps,
    completedReps = session.completedReps,
    averageScore = session.averageScore,
    repScores = repetitions.sortedBy { it.repNumber }.map { it.score },
    modelVersion = session.modelVersion,
)
