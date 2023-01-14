package com.android.device.businesslogic.entities

interface ScannerGateway {

    fun getScanResult() : List<DeviceController>

}