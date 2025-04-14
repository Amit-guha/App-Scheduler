package com.example.appscheduler.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppSchedule::class], version = 1)
abstract class AppScheduleDatabase : RoomDatabase() {
    abstract fun appScheduleDao(): AppScheduleDao
}