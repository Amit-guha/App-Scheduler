package com.example.appscheduler.view.adapter

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appscheduler.databinding.ItemInstalledAppBinding

class InstalledAppAdapter(
    private val appList: List<ApplicationInfo>,
    private val packageManager: PackageManager,
    private val onAppClick: (ApplicationInfo) -> Unit
) : RecyclerView.Adapter<InstalledAppAdapter.AppViewHolder>() {

    inner class AppViewHolder(private val binding: ItemInstalledAppBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appInfo: ApplicationInfo) {
            val appName = appInfo.loadLabel(packageManager).toString()
            val appIcon = appInfo.loadIcon(packageManager)

            binding.textViewAppName.text = appName
            binding.imageViewAppIcon.setImageDrawable(appIcon)

            binding.root.setOnClickListener {
                onAppClick(appInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemInstalledAppBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(appList[position])
    }

    override fun getItemCount(): Int = appList.size
}