package com.android.device.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import com.android.device.businesslogic.entities.Device

class BleScanCallBack(

    private val onScanResultAction: (ScanResult?) -> Unit = {},
    private val onBatchScanResultAction: (MutableList<ScanResult>?) -> Unit = {},
    private val onScanFailedAction: (Int) -> Unit = {}
): ScanCallback() {

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        this.onScanResultAction(result)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)
        this.onBatchScanResultAction(results)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        this.onScanFailedAction(errorCode)
    }


}