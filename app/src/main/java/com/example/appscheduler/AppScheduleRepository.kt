package com.example.appscheduler

import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class AppScheduleRepository @Inject constructor(
    private val appScheduleDao: AppScheduleDao
) {
    suspend fun insertSchedule(schedule: AppSchedule) = appScheduleDao.insert(schedule)
    suspend fun updateSchedule(schedule: AppSchedule) = appScheduleDao.update(schedule)
    suspend fun deleteSchedule(schedule: AppSchedule) = appScheduleDao.delete(schedule)
    fun getAllSchedules() = appScheduleDao.getAllSchedules()
    suspend fun getScheduleById(id: Int) = appScheduleDao.getScheduleById(id)
    suspend fun cancelSchedule(id: Int) = appScheduleDao.cancelSchedule(id)

}