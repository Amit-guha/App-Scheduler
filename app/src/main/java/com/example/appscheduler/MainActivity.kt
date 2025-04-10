package com.example.appscheduler

import android.app.AlarmManager
import android.app.PendingIntent
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel : AppSchedulerViewModel by viewModels()

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
    }

    private fun initUi() {
        setInstallListAdapter()
    }

    private fun setInstallListAdapter() {

        val adapter = InstalledAppAdapter(viewModel.getInstalledApps(), packageManager) { appInfo ->
            Toast.makeText(this, "Clicked: ${appInfo.loadLabel(packageManager)}", Toast.LENGTH_SHORT).show()
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