package com.android.device

import com.android.device.businesslogic.entities.Device
import com.android.device.businesslogic.entities.DeviceController
import org.junit.Assert
import org.junit.Test




class DeviceControllerTest {

    fun createDeviceController(isConnected: Boolean, isThirdParty: Boolean): DeviceController {
        val device = Device()
        device.isThirdParty = isThirdParty
        val deviceController = DeviceController(device)

        if(isConnected) {
            deviceController.onConnect()
            return deviceController
        } else {
            deviceController.onDisconnect()
            return deviceController
        }
    }

    @Test
    fun connectedThirdPartyDeviceFriendlyNameShouldNotBeTheDefaultNetworkName() {
        val deviceController = this.createDeviceController(true,true)
        Assert.assertNotEquals(deviceController.DEFAULT_NETWORK_NAME, deviceController.device.friendlyName);
    }

    @Test
    fun connectedThirdPartyDeviceFriendlyNameShouldNotBeTheBleNetworkName() {
        val deviceController = this.createDeviceController(true,true)
        Assert.assertNotEquals(deviceController.BLE_NETWORK_NAME, deviceController.device.friendlyName);
    }

    @Test
    fun connectedThirdPartyDeviceMeshNameShouldBeTheDefaultNetworkName() {
        val deviceController = this.createDeviceController(true,true)
        Assert.assertEquals(deviceController.DEFAULT_NETWORK_NAME, deviceController.meshName)
    }

    @Test
    fun connectedNotThirdPartyDeviceMeshNameShouldNotBeTheDefaultNetworkName() {
        val deviceController = this.createDeviceController(true, false)
        Assert.assertNotEquals(deviceController.DEFAULT_NETWORK_NAME, deviceController.meshName)
    }

    @Test
    fun connectedNotThirdPartyDeviceMeshNameShouldBeTheBLENetworkName() {
        val deviceController = this.createDeviceController(true,false)
        Assert.assertEquals(deviceController.BLE_NETWORK_NAME, deviceController.meshName)
    }

    @Test
    fun connectedNotThirdPartyDeviceFriendlyNameShouldBeTheBLENetworkName() {
        val deviceController = this.createDeviceController(true,false)
        Assert.assertEquals(deviceController.BLE_NETWORK_NAME, deviceController.device.friendlyName)
    }

    @Test
    fun unconnectedDeviceFriendlyNameShouldBeTheDefaultNetworkName() {
        val deviceController = this.createDeviceController(false, false)
        Assert.assertEquals(deviceController.DEFAULT_NETWORK_NAME, deviceController.device.friendlyName)
    }

    @Test
    fun unconnectedThirdPartyDeviceFriendlyNameShouldBeTheDefaultNetworkName() {
        val deviceController = this.createDeviceController(false,true)
        Assert.assertEquals(deviceController.DEFAULT_NETWORK_NAME, deviceController.device.friendlyName)
    }
}