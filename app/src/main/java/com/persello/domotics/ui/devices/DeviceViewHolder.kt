package com.persello.domotics.ui.devices

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.persello.domotics.R
import com.persello.domotics.data.device.DeviceData

class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private fun getDeviceImageResource(type: String): Int {
        return when (type) {
            "rolling shutter", "garage door" -> R.drawable.ic_settings
            "light" -> R.drawable.ic_heart
            else -> R.drawable.ic_devices
        }
    }

    fun bind(device: DeviceData) {
        itemView.findViewById<TextView>(R.id.textViewDeviceId).text = device.id;
        itemView.findViewById<ImageView>(R.id.imageViewDevice).setImageResource(getDeviceImageResource(device.type));

        val progressBarView = itemView.findViewById<ProgressBar>(R.id.progressBarDevice);

        if (device.type === "light") {
            progressBarView.progress = device.power;
        } else {
            progressBarView.progress = device.opening;
        }
    }
}