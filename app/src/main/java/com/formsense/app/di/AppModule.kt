package com.formsense.app.di

import android.content.Context
import androidx.room.Room
import com.formsense.app.data.local.FormSenseDatabase
import com.formsense.app.data.repository.RoomSessionRepository
import com.formsense.app.data.repository.SessionRepository
import com.formsense.app.data.repository.DataStoreSettingsRepository
import com.formsense.app.data.repository.SettingsRepository
import com.formsense.app.domain.analyzer.FakePostureAnalyzer
import com.formsense.app.domain.analyzer.PostureAnalyzer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindSessionRepository(repository: RoomSessionRepository): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(repository: DataStoreSettingsRepository): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindPostureAnalyzer(analyzer: FakePostureAnalyzer): PostureAnalyzer
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): FormSenseDatabase = Room.databaseBuilder(
        context,
        FormSenseDatabase::class.java,
        "formsense.db",
    ).build()
}
