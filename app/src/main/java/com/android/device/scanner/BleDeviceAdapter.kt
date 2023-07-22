package com.android.device.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
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

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BleDeviceAdapter.ViewHolder, position: Int) {
        val device = devices[position]

        holder.deviceAddressTextView.text = device.address
        holder.deviceIsConnectableTextView.text = "is connectable " + device.isConnectable.toString()
        holder.deviceNameTextView.text = device.name

        if(device.isConnectable) {
            val v = holder.itemView
            v.setBackgroundColor(v.resources.getColor(R.color.teal_200, v.resources.newTheme()))
            v.setOnClickListener {v ->
                this.connectInterface.connect(device as ConnectableBleDevice)
                v.alpha = 0.5F
            }
        }

    }

    override fun getItemCount(): Int {
        return devices.size
    }

    interface Connect {
        fun connect(bleDevice: ConnectableBleDevice)
    }
}