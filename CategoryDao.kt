package com.example.pennywise4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM Category")
    suspend fun getAllCategories(): List<Category>
}