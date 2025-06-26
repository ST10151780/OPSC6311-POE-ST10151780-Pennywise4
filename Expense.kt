package com.example.pennywise4

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val category: String,
    val amount: Double,
    val photoUri: String?, // nullable
    val minGoal: Double?,
    val maxGoal: Double?
)