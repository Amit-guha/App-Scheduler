package com.example.appscheduler

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appscheduler.databinding.ItemScheduleBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ScheduleListAdapter(
    private val packageManager: PackageManager,
    private val listener: OnScheduleActionListener
) : ListAdapter<AppSchedule, ScheduleListAdapter.ScheduleViewHolder>(ScheduleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScheduleViewHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun bind(schedule: AppSchedule) {
            try {
                val appInfo = packageManager.getApplicationInfo(schedule.packageName, 0)
                val appName = appInfo.loadLabel(packageManager).toString()
                val appIcon = appInfo.loadIcon(packageManager)

                binding.textViewAppName.text = appName
                binding.appIcon.setImageDrawable(appIcon)
            } catch (e: Exception) {
                binding.textViewAppName.text = schedule.packageName
            }

            binding.textViewScheduleTime.text = dateFormat.format(schedule.scheduledTime)
            binding.status.text = if (schedule.isExecuted) "Executed" else "Pending"

            binding.btnEdit.setOnClickListener {
                listener.onEditSchedule(schedule)
            }

            binding.btnCancel.setOnClickListener {
                listener.onCancelSchedule(schedule)
            }
        }
    }

    interface OnScheduleActionListener {
        fun onEditSchedule(schedule: AppSchedule)
        fun onCancelSchedule(schedule: AppSchedule)
    }
}

private class ScheduleDiffCallback : DiffUtil.ItemCallback<AppSchedule>() {
    override fun areItemsTheSame(oldItem: AppSchedule, newItem: AppSchedule): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AppSchedule, newItem: AppSchedule): Boolean {
        return oldItem == newItem
    }
}