package com.example.appscheduler.view

import android.R
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appscheduler.databinding.ActivityMainBinding
import com.example.appscheduler.model.AppSchedule
import com.example.appscheduler.utils.AlarmPermissionUtils
import com.example.appscheduler.utils.CustomDialog
import com.example.appscheduler.utils.showToast
import com.example.appscheduler.view.adapter.InstalledAppAdapter
import com.example.appscheduler.view.adapter.ScheduleListAdapter
import com.example.appscheduler.viewmodel.AppSchedulerViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var scheduleAdapter: ScheduleListAdapter
    private val customDialog by lazy { CustomDialog(this) }
    private lateinit var alarmPermissionLauncher: ActivityResultLauncher<Intent>
    private val viewModel: AppSchedulerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestExactAlarmPermission()
        initUi()
        initListeners()
        observeLiveData()
    }

    private fun checkAndRequestExactAlarmPermission() {
        triggerAlarmPermissionLauncher()
        checkExactAlarmPermission()
    }


    private fun triggerAlarmPermissionLauncher() {
        alarmPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            handleExactAlarmPermissionResult()
        }
    }


    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!AlarmPermissionUtils.canScheduleExactAlarms(this)) {
                showExactAlarmPermissionDialog()
            }
        }
    }

    private fun initListeners() {
        binding.fabAdd.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!AlarmPermissionUtils.canScheduleExactAlarms(this)) {
                    showExactAlarmPermissionDialog()
                } else {
                    viewModel.setInstalledAppsView(true)
                }
            } else {
                viewModel.setInstalledAppsView(true)
            }
        }

        binding.btnCancel.setOnClickListener {
            viewModel.setInstalledAppsView(false)
        }
    }

    private fun toggleAppList(show: Boolean) {
        binding.textViewNoScheduledApps.visibility = when {
            show -> View.GONE
            viewModel.allSchedules.value.isNullOrEmpty() -> View.VISIBLE
            else -> View.GONE
        }
        binding.appListContainer.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnCancel.visibility = if (show) View.VISIBLE else View.GONE
        binding.fabAdd.visibility = if (show) View.GONE else View.VISIBLE
        binding.scheduleRecyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showExactAlarmPermissionDialog() {
        customDialog.showPermissionDialog(
            onPermissionGranted = {
                requestExactAlarmPermission()
            },
            onPermissionDenied = {
                showToast(getString(com.example.appscheduler.R.string.permission_denied_enable_it_later_in_settings_alarms_reminders))
            }
        )
    }


    private fun observeLiveData() {
        viewModel.allSchedules.observe(this) { schedules ->
            if (schedules.isEmpty()) {
                binding.textViewNoScheduledApps.visibility = View.VISIBLE
            } else {
                binding.textViewNoScheduledApps.visibility = View.GONE
            }
            scheduleAdapter.submitList(schedules)
        }

        viewModel.isInInstalledAppsView.observe(this) {
            toggleAppList(it)
        }
    }

    private fun initUi() {
        setInstallListAdapter()
        setScheduleListAdapter()
    }

    private fun setScheduleListAdapter() {
        scheduleAdapter = ScheduleListAdapter(
            packageManager,
            object : ScheduleListAdapter.OnScheduleActionListener {
                override fun onEditSchedule(schedule: AppSchedule) {
                    showHourPicker(context = this@MainActivity) {
                        viewModel.rescheduleApp(
                            id = schedule.id,
                            newTime = it
                        )
                    }
                }

                override fun onCancelSchedule(schedule: AppSchedule) {
                    viewModel.cancelSchedule(id = schedule.id)
                    showToast(getString(com.example.appscheduler.R.string.schedule_canceled_successfully))
                }

            })

        binding.scheduleRecyclerView.adapter = scheduleAdapter
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setInstallListAdapter() {
        val adapter = InstalledAppAdapter(viewModel.getInstalledApps(), packageManager) { appInfo ->
            showHourPicker(context = this) {
                viewModel.scheduleApp(
                    packageName = appInfo.packageName,
                    appName = appInfo.loadLabel(packageManager).toString(),
                    time = it
                )
            }
        }

        binding.appRecyclerView.adapter = adapter
        binding.appRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:$packageName".toUri()
                }
                alarmPermissionLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:$packageName".toUri()
                }
                alarmPermissionLauncher.launch(intent)
            }
        }
    }

    private fun handleExactAlarmPermissionResult() {
        if (!AlarmPermissionUtils.canScheduleExactAlarms(this)) {
            showToast(message = getString(com.example.appscheduler.R.string.permission_denied_enable_it_later_in_settings_alarms_reminders))
        }
    }
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




