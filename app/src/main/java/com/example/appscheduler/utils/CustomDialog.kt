package com.example.appscheduler.utils

import android.app.AlertDialog
import android.content.Context
import com.example.appscheduler.R

class CustomDialog(
    private val context: Context,

    ) {
    fun showPermissionDialog(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.exact_alarm_permission_needed))
            .setMessage(context.getString(R.string.this_app_needs_exact_alarm_permission_to_work_properly_please_grant_the_permission))
            .setPositiveButton(context.getString(R.string.allow)) { _, _ ->
                onPermissionGranted()
            }
            .setNegativeButton(context.getString(R.string.deny)) { _, _ ->
                onPermissionDenied()
            }
            .setCancelable(false)
            .show()
    }
}