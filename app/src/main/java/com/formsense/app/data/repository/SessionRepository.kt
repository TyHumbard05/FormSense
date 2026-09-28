package com.formsense.app.data.repository

import com.formsense.app.data.model.CompletedSessionDraft
import com.formsense.app.data.model.RecentSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeRecentSessions(limit: Int = 3): Flow<List<RecentSession>>

    fun observeAllSessions(): Flow<List<RecentSession>>

    fun observeSession(sessionId: Long): Flow<RecentSession?>

    suspend fun saveCompletedSession(draft: CompletedSessionDraft): Long

    suspend fun deleteSession(sessionId: Long)

    suspend fun deleteAllSessions()
}
