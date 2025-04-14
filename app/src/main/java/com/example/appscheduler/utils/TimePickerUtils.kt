package com.example.appscheduler.utils


import android.R
import android.app.TimePickerDialog
import android.content.Context
import com.example.appscheduler.model.AppSchedule
import java.util.Calendar

class TimePickerUtils(private val context : Context) {

    fun showHourPicker(
        existingSchedule: AppSchedule? = null,
        onTimeSelected: (Long) -> Unit
    ) {

        val calender = Calendar.getInstance()
        existingSchedule?.let {
            calender.timeInMillis = it.scheduledTime
        } ?: run {
            calender.timeInMillis = System.currentTimeMillis()
        }

        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        val onTimeSelectedListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calender.set(Calendar.MINUTE, minute)
            calender.set(Calendar.SECOND, 0)
            calender.set(Calendar.MILLISECOND, 0)

            var selectedTime = calender.timeInMillis
            if (selectedTime < System.currentTimeMillis()) {
                calender.add(Calendar.DAY_OF_YEAR, 1)
                selectedTime = calender.timeInMillis
            }
            onTimeSelected(selectedTime)
        }

        val timePickerDialog = TimePickerDialog(
            context,
            R.style.Theme_Holo_Light_Dialog_NoActionBar,
            onTimeSelectedListener,
            hour,
            minute,
            true
        )

        timePickerDialog.setTitle(context.getString(com.example.appscheduler.R.string.choose_hour))
        timePickerDialog.window?.setBackgroundDrawableResource(R.color.transparent)
        timePickerDialog.show()
    }
}