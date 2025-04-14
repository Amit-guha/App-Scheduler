package com.example.appscheduler.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppScheduleDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(schedule: AppSchedule): Long

    @Update
    suspend fun update(schedule: AppSchedule)

    @Delete
    suspend fun delete(schedule: AppSchedule)

    @Query("SELECT * FROM scheduled_apps ORDER BY scheduledTime ASC")
    fun getAllSchedules(): LiveData<List<AppSchedule>>

    @Query("SELECT * FROM scheduled_apps WHERE id = :id")
    suspend fun getScheduleById(id: Int): AppSchedule?

    @Query("DELETE FROM scheduled_apps WHERE id = :id")
    suspend fun cancelSchedule(id: Int)
}