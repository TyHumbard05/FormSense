package com.formsense.app.data.repository

import com.formsense.app.data.local.dao.UserDao
import com.formsense.app.data.local.entity.UserEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val settingsRepository: SettingsRepository,
) {
    val activeUser: Flow<UserEntity?> = settingsRepository.settings
        .flatMapLatest { settings ->
            val userId = settings.activeUserId
            if (userId != null && userId > 0) {
                userDao.observeUserById(userId)
            } else {
                flowOf(null)
            }
        }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.getUserByEmail(email.trim().lowercase())
                ?: return Result.failure(Exception("No account found with this email"))

            if (user.passwordHash != password) {
                return Result.failure(Exception("Incorrect password"))
            }

            settingsRepository.setActiveUserId(user.id)
            Result.success(user)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<UserEntity> {
        return try {
            val cleanEmail = email.trim().lowercase()
            if (userDao.getUserByEmail(cleanEmail) != null) {
                return Result.failure(Exception("An account with this email already exists"))
            }

            val newUser = UserEntity(
                fullName = name.trim(),
                email = cleanEmail,
                passwordHash = password,
            )
            val id = userDao.insertUser(newUser)
            val createdUser = newUser.copy(id = id)
            settingsRepository.setActiveUserId(id)
            Result.success(createdUser)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun updateUserName(userId: Long, newName: String) {
        if (userId <= 0) return
        try {
            val user = userDao.getUserById(userId) ?: return
            userDao.updateUser(user.copy(fullName = newName.trim()))
        } catch (e: Exception) {
            if (e is CancellationException) throw e
        }
    }

    suspend fun logout() {
        settingsRepository.setActiveUserId(null)
    }
}
