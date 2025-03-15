package com.persello.domotics.ui.devices

import androidx.recyclerview.widget.DiffUtil
import com.persello.domotics.data.device.DeviceData

class DeviceDiffCallback(
    private val oldList: List<DeviceData>,
    private val newList: List<DeviceData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size;
    }

    override fun getNewListSize(): Int {
        return newList.size;
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id;
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition];
    }
}