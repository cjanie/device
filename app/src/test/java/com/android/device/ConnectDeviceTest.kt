package com.android.device

import com.android.device.businesslogic.NetworkEnum
import com.android.device.businesslogic.entities.Device
import com.android.device.businesslogic.entities.DeviceController
import org.junit.Assert
import org.junit.Test

class ConnectDeviceTest {

    @Test
    fun connectShouldSetTheNetworkUserName() {
        val deviceController = DeviceController(Device())
        deviceController.connect()
        Assert.assertEquals(NetworkEnum.USER_NETWORK_NAME, deviceController.getUserNetworkName())
    }
}