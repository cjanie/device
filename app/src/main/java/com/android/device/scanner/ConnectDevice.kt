package com.android.device.scanner

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat

class ConnectDevicePermissionCallBack(val device: ConnectableBleDevice): ActivityResultCallback<Boolean> {

    override fun onActivityResult(result: Boolean?) {

    }


}