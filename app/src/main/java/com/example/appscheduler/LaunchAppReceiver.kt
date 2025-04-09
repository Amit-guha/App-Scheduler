package com.example.appscheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast


class LaunchAppReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
       // val packageName = "com.miui.notes" // YouTube app
       // val packageName = "com.google.android.apps.youtube.music" // YouTube app
        val packageName = "com.google.android.youtube" // YouTube app
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (app in apps) {
            val pkg = app.packageName.toString()
            Log.d("Package Name on Install App", "onReceive: ${pkg}.")
        }



        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        } else {
            Toast.makeText(context, "YouTube is not installed.", Toast.LENGTH_LONG).show()
        }
    }
}
