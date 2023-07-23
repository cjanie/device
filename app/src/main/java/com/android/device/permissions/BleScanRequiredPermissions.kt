package com.android.device.permissions

import android.Manifest
import android.os.Build

class BleScanRequiredPermissions {

    val permissions = this.setUpSdkPermissions()

    fun setUpSdkPermissions(): Array<String> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                //if you want your device to be discoverable you also need this permission ->
                //Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            return arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }


}