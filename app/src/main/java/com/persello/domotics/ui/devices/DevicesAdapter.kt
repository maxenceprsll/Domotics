package com.persello.domotics.ui.devices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.persello.domotics.R
import com.persello.domotics.data.device.DeviceData

class DevicesAdapter : RecyclerView.Adapter<DeviceViewHolder>()
{

    private var devices = emptyList<DeviceData>();

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.devices_list_item, parent, false).apply{
                tag = devices[viewType];
            };
        return DeviceViewHolder(view);
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position]);
        holder.itemView.tag = devices[position];
    }

    override fun getItemCount(): Int {
        return devices.size;
    }

    fun setDevices(devices: List<DeviceData>) {
        this.devices = devices;
        notifyItemRangeChanged(0, devices.size);
    }
}
