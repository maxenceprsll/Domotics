package com.persello.domotics.ui.devices

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.persello.domotics.R
import com.persello.domotics.data.device.DeviceData

class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private fun getDeviceImageResource(device: DeviceData): Int {
        val type = device.type;
        val opening = device.opening;
        val power = device.power;

        if (type == "sliding shutter" || type == "rolling shutter") {
            if (opening == 0) {
                return R.drawable.window_shutter;
            } else if (opening == 1) {
                return R.drawable.window_shutter_open;
            } else if (opening > 0 && opening < 1) {
                return R.drawable.window_shutter_alert;
            } else {
                return R.drawable.alert_circle_outline;
            }
        } else if (type == "garage door") {
            if (opening == 0) {
                return R.drawable.garage;
            } else if (opening == 1) {
                return R.drawable.garage_open;
            } else if (opening > 0 && opening < 1) {
                return R.drawable.garage_alert;
            } else {
                return R.drawable.alert_circle_outline;
            }
        } else if (type == "light") {
            if (power == 0) {
                return R.drawable.lightbulb;
            } else if (power == 1){
                return R.drawable.lightbulb_on;
            } else {
                return R.drawable.lightbulb_alert;
            }
        } else {
            if (opening == 0 || power == 0) {
                return R.drawable.power_plug_off;
            } else if (opening == 1 || power == 1) {
                return R.drawable.power_plug;
            } else {
                return R.drawable.alert_circle_outline;
            }
        }
    }

    fun bind(device: DeviceData) {
        itemView.findViewById<TextView>(R.id.textViewDeviceId).text = device.id;


        val progressBarView = itemView.findViewById<ProgressBar>(R.id.progressBarDevice);

        if (device.type === "light") {
            progressBarView.progress = device.power;
        } else {
            progressBarView.progress = device.opening;
        }

        itemView.findViewById<ImageView>(R.id.imageViewDevice).setImageResource(getDeviceImageResource(device));
    }
}