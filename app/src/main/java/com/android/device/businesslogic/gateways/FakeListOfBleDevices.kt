package com.android.device.businesslogic.gateways

import com.android.device.scanner.BleDevice

class FakeListOfBleDevices {



    init {
        this.list()
    }

    fun list(): MutableList<BleDevice> {
        // initialize list
        val foundDevices = BleDevice.createBleDevicesList()
        // FAKE
        val bleDevice1 = BleDevice("â^^%%", true, "nano")
        foundDevices.add(bleDevice1)
        val bleDevice2 = BleDevice("azzaa@@@", false, "jojo")
        foundDevices.add(bleDevice2)
        val bleDevice3 = BleDevice("aafs@sa", true, "lola")
        foundDevices.add(bleDevice3)
        return foundDevices
    }
}