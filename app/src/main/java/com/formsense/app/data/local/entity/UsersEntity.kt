package com.formsense.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)], // Index for fast login & duplicate prevention
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val age: String = "",
    val weight: String = "",
    val height: String = "",
    val goal: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
)