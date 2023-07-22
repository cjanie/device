package com.android.device.scanner

open class BleDevice(val address: String, val isConnectable: Boolean, val name: String?) {

    companion object {
        // method to initialize an empty list
        fun createBleDevicesList(): MutableList<BleDevice> {
            return mutableListOf()
        }
    }

}