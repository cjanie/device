package com.android.device

import com.android.device.businesslogic.NetworkConstants
import com.android.device.businesslogic.entities.Device
import com.android.device.businesslogic.entities.DeviceController
import org.junit.Assert
import org.junit.Test

class ConnectDeviceTest {

    @Test
    fun connectShouldSetTheNetworkUserName() {
        val deviceController = DeviceController(Device())
        deviceController.connect()
        Assert.assertEquals(NetworkConstants.NETWORK_USER_NAME, deviceController.getNetworkUserName())
    }
}