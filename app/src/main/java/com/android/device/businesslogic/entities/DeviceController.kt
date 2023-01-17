package com.android.device.businesslogic.entities

import com.android.device.businesslogic.NetworkConstants
import com.android.device.businesslogic.exceptions.BleConnectionException

// To connect one device from the scan results,
// Use a controller that encapsulate the device

class DeviceController(val device: Device) {

    private var connection = false

    fun connect() {
        this.connection = true
    }

    fun getNetworkUserName(): NetworkConstants {
        if(this.connection) {
            return NetworkConstants.NETWORK_USER_NAME
        }
        throw BleConnectionException()
    }
}