package com.example.appscheduler.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scheduled_apps")
data class AppSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val appName: String,
    val scheduledTime: Long,
    val isExecuted: Boolean = false
)