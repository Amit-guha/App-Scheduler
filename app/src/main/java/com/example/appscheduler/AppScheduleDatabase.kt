package com.example.appscheduler

import androidx.room.RoomDatabase

@androidx.room.Database(entities = [AppSchedule::class], version = 1)
abstract class AppScheduleDatabase : RoomDatabase() {
    abstract fun appScheduleDao(): AppScheduleDao
}