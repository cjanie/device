package com.android.device.scanner

data class BleDevice(val name: String) {

    companion object {
        fun createBleDevicesList(): MutableList<BleDevice> {
            return mutableListOf()
        }
    }

}