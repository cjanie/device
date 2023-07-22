package com.android.device.permissions

import android.Manifest

class BleScanRequiredPermissions {

    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        //Manifest.permission.BLUETOOTH_SCAN,
        //Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        //Manifest.permission.ACCESS_COARSE_LOCATION,
    )
}