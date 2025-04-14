package com.example.appscheduler

import android.app.AlarmManager
import android.content.Context
import android.os.Build

object AlarmPermissionUtils {
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun requiresExactAlarmPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

}