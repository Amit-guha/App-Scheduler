package com.example.appscheduler

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSchedulerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appScheduleRepository: AppScheduleRepository
) : ViewModel() {

    val allSchedules: LiveData<List<AppSchedule>> = appScheduleRepository.getAllSchedules()

    fun getInstalledApps(): List<ApplicationInfo> {
        val pm = context.packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
    }

    fun scheduleApp(packageName: String, appName: String, time: Long) {
        viewModelScope.launch {
            val schedule = AppSchedule(packageName = packageName, appName = appName, scheduledTime = time)
            val insertedId = appScheduleRepository.insertSchedule(schedule)
            val updatedSchedule = schedule.copy(id = insertedId.toInt())
           // AlarmScheduler.setAlarm(context, updatedSchedule)
        }
    }



}