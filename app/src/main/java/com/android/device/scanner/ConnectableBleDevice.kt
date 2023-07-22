package com.android.device.scanner

import android.bluetooth.BluetoothDevice

class ConnectableBleDevice(address: String, isConnectable: Boolean, name: String?,
                           val btDevice: BluetoothDevice)
    : BleDevice(address, isConnectable, name)