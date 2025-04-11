package com.example.appscheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appscheduler.databinding.ActivityMainBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AppSchedulerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = "package:$packageName".toUri()
                Toast.makeText(
                    this,
                    "$packageName + needs permission to schedule exact alarms..",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(intent)
                return
            }
        }

        // scheduleYouTubeApp(context = this, timeInMillis = System.currentTimeMillis() + 5000)


        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        initUi()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.allSchedules.observe(this) { schedules ->
            Toast.makeText(this, "Schedules updated: $schedules", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initUi() {
        setInstallListAdapter()
    }

    private fun setInstallListAdapter() {
        val adapter = InstalledAppAdapter(viewModel.getInstalledApps(), packageManager) { appInfo ->
            Toast.makeText(
                this,
                "Clicked: ${appInfo.loadLabel(packageManager)}",
                Toast.LENGTH_SHORT
            ).show()
            /*     showTimePicker(context = this) {
                     Toast.makeText(this, "Clicked: $it", Toast.LENGTH_SHORT).show()
                     // scheduleYouTubeApp(context = this, timeInMillis = it)
                 }*/

            showHourPicker(context = this) {
                viewModel.scheduleApp(
                    packageName = appInfo.packageName,
                    appName = appInfo.loadLabel(packageManager).toString(),
                    time = it
                )
                Toast.makeText(this, "Clicked: $it", Toast.LENGTH_SHORT).show()
            }
        }

        binding.appRecyclerView.adapter = adapter
        binding.appRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}

private fun scheduleYouTubeApp(context: Context, timeInMillis: Long) {
    val intent = Intent(context, LaunchAppReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        timeInMillis,
        pendingIntent
    )
}

private fun showTimePicker(
    context: Context,
    existingSchedule: AppSchedule? = null,
    onTimeSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance()
    existingSchedule?.let {
        calendar.timeInMillis = it.scheduledTime
    }

    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            val selectedTime = calendar.timeInMillis
            onTimeSelected(selectedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}

fun showHourPicker(
    context: Context,
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

        var selectedTime = calender.timeInMillis
        if (selectedTime < System.currentTimeMillis()) {
            calender.add(Calendar.DAY_OF_YEAR, 1)
            selectedTime = calender.timeInMillis
        }
        onTimeSelected(selectedTime)
    }

    val timePickerDialog = TimePickerDialog(
        context,
        android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
        onTimeSelectedListener,
        hour,
        minute,
        true
    )

    timePickerDialog.setTitle("Choose hour:")
    timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    // Show the dialog
    timePickerDialog.show()
}


fun showMeterial3HourPicker(
    context: Context,
    existingSchedule: AppSchedule? = null,
    onTimeSelected: (Long) -> Unit
) {
    // Get the current calendar instance
    val calendar = Calendar.getInstance()

    // If an existing schedule is provided, use its time; otherwise, use the current time
    existingSchedule?.let {
        calendar.timeInMillis = it.scheduledTime
    } ?: run {
        // If no schedule exists, default to the current time
        calendar.timeInMillis = System.currentTimeMillis()
    }

    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    // Create a MaterialTimePicker instance
    val timePicker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H) // Use 24-hour format (set to CLOCK_12H for 12-hour format)
        .setHour(hour)
        .setMinute(minute)
        .setTitleText("Choose hour:")
        .build()

    // Listener for time selection
    timePicker.addOnPositiveButtonClickListener {
        // Get the selected hour and minute
        val selectedHour = timePicker.hour
        val selectedMinute = timePicker.minute

        // Set the time in the calendar
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)

        // Check if the selected time is in the past
        var selectedTime = calendar.timeInMillis
        if (selectedTime < System.currentTimeMillis()) {
            // If the selected time is in the past, adjust to the next day
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            selectedTime = calendar.timeInMillis
        }

        // Pass the selected time to the callback
        onTimeSelected(selectedTime)
    }

    // Listener for cancel button
    timePicker.addOnNegativeButtonClickListener {
        Toast.makeText(context, "Time selection canceled", Toast.LENGTH_SHORT).show()
    }

    // Show the time picker
    timePicker.show((context as AppCompatActivity).supportFragmentManager, "time_picker")
}



