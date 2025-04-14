package com.example.appscheduler.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.appscheduler.model.AppSchedule
import com.example.appscheduler.receiver.LaunchAppReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
     private val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)

    fun setAlarm(schedule: AppSchedule) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager?.canScheduleExactAlarms() != true) {
            context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
            return
        }

        val intent = Intent(context, LaunchAppReceiver::class.java).apply {
            action = "com.example.appscheduler.APP_LAUNCH_ACTION"
            putExtra("schedule_id", schedule.id)
            putExtra("package_name", schedule.packageName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        try {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                schedule.scheduledTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Permission denied to schedule exact alarm", e)
        }

    }


    fun cancelAlarm(scheduleId: Int) {
        val intent = Intent(context, LaunchAppReceiver::class.java).apply {
            action = "com.example.appscheduler.APP_LAUNCH_ACTION"
            putExtra("schedule_id", scheduleId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager?.cancel(pendingIntent)
    }

}