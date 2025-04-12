package com.example.appscheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LaunchAppReceiver : BroadcastReceiver() {
    @Inject
    lateinit var repository: AppScheduleRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "com.example.appscheduler.APP_LAUNCH_ACTION") return
        val packageName = intent.getStringExtra("package_name") ?: return
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(launchIntent)

        val scheduleId = intent.getIntExtra("schedule_id", -1)
        if (scheduleId == -1) return


        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            val schedule = repository.getScheduleById(scheduleId)
            schedule?.let {
                repository.updateSchedule(it.copy(isExecuted = true))
            }
        }
    }

}

