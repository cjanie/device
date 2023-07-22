package com.android.device.scanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.device.R

class BleDeviceAdapter(private val devices: List<BleDevice>, private val connectInterface: Connect)
    : RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceAddressTextView: TextView = itemView.findViewById(R.id.device_address)
        val deviceIsConnectableTextView: TextView = itemView.findViewById(R.id.device_isConnectable)
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

        holder.deviceAddressTextView.text = device.address
        holder.deviceIsConnectableTextView.text = "is connectable " + device.isConnectable.toString()
        holder.deviceNameTextView.text = device.name

        // OnClick on itemView -> connect()
        holder.itemView.setOnClickListener {v ->
                this.connectInterface.connect(device.address)
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    interface Connect {
        fun connect(address: String)
    }
}