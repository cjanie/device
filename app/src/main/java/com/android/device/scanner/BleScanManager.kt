package com.android.device.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.os.Handler
import android.os.Looper

class BleScanManager(
    btManager: BluetoothManager,
    private val scanPeriod: Long = DEFAULT_SCAN_PERIOD,
    private val scanCallBack: BleScanCallBack = BleScanCallBack()
) {

    private val btAdapter = btManager.adapter

    private val bleScanner = btAdapter.bluetoothLeScanner

    var beforeScanActions: MutableList<() -> Unit> = mutableListOf()
    var  afterScanActions: MutableList<() -> Unit> = mutableListOf()

    // True when the manager is performing the scan
    private var  scanning = false

    private val handler = Handler(Looper.getMainLooper())

    /**
     * Scans for Bluetooth LE devices and stops the scan after [scanPeriod] seconds.
     * Does not checks the required permissions are granted, check must be done beforehand.
     */

    @SuppressLint("MissingPermission")
    fun scanBleDevices() {
        fun stopScan() {
            this.scanning = false

            // scanner is null if bluetooth is not enabled on the device
            this.bleScanner?.stopScan(this.scanCallBack)

            // execute all the functions to execute after scanning
            this.executeAfterScanActions()
        }

        // scans for bluetooth LE devices
        if (this.scanning) {
            stopScan()
        } else {
            // stops scanning after scanPeriod millis
            this.handler.postDelayed({ stopScan() }, this.scanPeriod)
            // execute all the functions to execute before scanning
            this.executeBeforeScanActions()

            // starts scanning
            this.scanning = true

            //try {
                this.startScan()
            /*} catch (e: BluetoothDisabledException) {
                throw e
            }*/
        }
    }

    private fun executeBeforeScanActions() {
        executeListOfFunctions(this.beforeScanActions)
    }

    private fun executeAfterScanActions() {
        executeListOfFunctions(this.afterScanActions)
    }

    companion object {
        const val DEFAULT_SCAN_PERIOD: Long = 10000

        private fun executeListOfFunctions(toExecute: List<() -> Unit>) {
            toExecute.forEach {
                it()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        // scanner is null if bluetooth is not enabled on the device
        this.bleScanner?: throw BluetoothDisabledException()

        this.bleScanner.startScan(this.scanCallBack)
    }
}