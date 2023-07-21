package com.android.device.businesslogic.entities

import com.android.device.businesslogic.NetworkEnum
import com.android.device.businesslogic.exceptions.BleConnectionException

// To connect one device from the scan results,
// Use a controller that encapsulate the device

class DeviceController(val device: Device) {

    private var connection = false

    fun connect() {
        this.connection = true
    }

    fun getUserNetworkName(): NetworkEnum {
        if(this.connection) {
            return NetworkEnum.USER_NETWORK_NAME
        }
        throw BleConnectionException()
    }
}