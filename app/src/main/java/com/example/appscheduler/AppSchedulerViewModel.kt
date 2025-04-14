package com.example.appscheduler

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSchedulerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appScheduleRepository: AppScheduleRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _isInInstalledAppsView = MutableLiveData<Boolean>(false)
    val isInInstalledAppsView: LiveData<Boolean> = _isInInstalledAppsView


    val allSchedules: LiveData<List<AppSchedule>> = appScheduleRepository.getAllSchedules()

    fun getInstalledApps(): List<ApplicationInfo> {
        val pm = context.packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
    }

    fun scheduleApp(packageName: String, appName: String, time: Long) {
        viewModelScope.launch {
            val schedule =
                AppSchedule(packageName = packageName, appName = appName, scheduledTime = time)
            val insertedId = appScheduleRepository.insertSchedule(schedule)
            val updatedSchedule = schedule.copy(id = insertedId.toInt())
            alarmScheduler.setAlarm(updatedSchedule)
        }
    }

    fun cancelSchedule(id: Int) {
        viewModelScope.launch {
            appScheduleRepository.cancelSchedule(id)
            alarmScheduler.cancelAlarm(id)
        }
    }

    fun rescheduleApp(id: Int, newTime: Long) {
        viewModelScope.launch {
            val schedule = appScheduleRepository.getScheduleById(id)
                ?.copy(scheduledTime = newTime, isExecuted = false)
            if (schedule != null) {
                appScheduleRepository.updateSchedule(schedule)
                alarmScheduler.setAlarm(schedule)
            }
        }
    }

    fun setInstalledAppsView(isVisible: Boolean) {
        _isInInstalledAppsView.value = isVisible
    }


}