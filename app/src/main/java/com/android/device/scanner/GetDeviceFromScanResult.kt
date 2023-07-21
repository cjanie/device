package com.android.device.scanner

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import com.android.device.businesslogic.entities.Device

class GetDeviceFromScanResult {

    fun getDeviceNameFromResult(result: ScanResult): String? {
        return result.scanRecord?.deviceName
    }

    fun isConnectableFromResult(result: ScanResult): Boolean {
        return result.isConnectable
    }

    fun getBluetoothDeviceFromResult(result: ScanResult): BluetoothDevice {
        return result.device
    }

    fun getDeviceAddressFromResult(result: ScanResult): String {
        return this.getBluetoothDeviceFromResult(result).address
    }

    fun createBleDeviceFromResult(result: ScanResult): BleDevice {

        val address = this.getDeviceAddressFromResult(result)
        val isConnectable = this.isConnectableFromResult(result)
        val name = this.getDeviceNameFromResult(result)

        val bleDevice = BleDevice(address, isConnectable, name)
        return bleDevice

    }

}