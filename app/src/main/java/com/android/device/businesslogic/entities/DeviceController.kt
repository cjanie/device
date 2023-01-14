package com.android.device.businesslogic.entities

class DeviceController(val device: Device) {

    val DEFAULT_NETWORK_NAME = "unpaired"
    val BLE_NETWORK_NAME = "bleNetworkName"

    var meshName = "";

    fun onConnect() {
        if(this.device.isThirdParty) {
            this.meshName = DEFAULT_NETWORK_NAME
        } else {
            this.meshName = BLE_NETWORK_NAME
            this.device.friendlyName = BLE_NETWORK_NAME
        }
    }

    fun onDisconnect() {
        this.meshName = ""
        this.device.friendlyName = DEFAULT_NETWORK_NAME
    }

}