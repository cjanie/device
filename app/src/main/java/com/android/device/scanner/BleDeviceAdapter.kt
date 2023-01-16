package com.android.device.scanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.device.R

class BleDeviceAdapter(private val devices: List<BleDevice>)
    : RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceNameTextView: TextView = itemView.findViewById(R.id.device_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val deviceView = inflater.inflate(R.layout.layout_device_list_item, parent, false)
        return ViewHolder(deviceView)
    }

    override fun onBindViewHolder(holder: BleDeviceAdapter.ViewHolder, position: Int) {
        val device = devices[position]
        val textView = holder.deviceNameTextView
        textView.text = device.name
    }

    override fun getItemCount(): Int {
        return devices.size
    }
}