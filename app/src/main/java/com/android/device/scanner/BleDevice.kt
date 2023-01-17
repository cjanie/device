package com.android.device.scanner

data class BleDevice(val address: String) {

    companion object {
        fun createBleDevicesList(): MutableList<BleDevice> {
            return mutableListOf()
        }
    }

}