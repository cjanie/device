package com.android.device.businesslogic.gateways

import com.android.device.businesslogic.entities.DeviceController

interface ScannerGateway {

    fun getScanResult() : List<DeviceController>

}