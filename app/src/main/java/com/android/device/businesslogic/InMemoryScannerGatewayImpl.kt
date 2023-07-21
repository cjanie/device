package com.android.device.businesslogic

import com.android.device.businesslogic.entities.Device
import com.android.device.businesslogic.entities.DeviceController
import com.android.device.businesslogic.gateways.ScannerGateway

class InMemoryScannerGatewayImpl : ScannerGateway {

    override fun getScanResult(): List<DeviceController> {
        val deviceControllers = ArrayList<DeviceController>()
        var i = 0
        while (i < 10) {
            i = i++
            deviceControllers.add(DeviceController(Device()))
        }
        return deviceControllers
    }

}