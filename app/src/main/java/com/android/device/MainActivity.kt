package com.android.device

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.device.permissions.BleScanRequiredPermissions
import com.android.device.scanner.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * As a user,
 * I want to see all the devices around me
 * and to know if they are connectable
 * + address, name?
 * data are provided by the ScanResult
 * */

class MainActivity : AppCompatActivity(), BleDeviceAdapter.Connect {

    // https://medium.com/geekculture/how-to-create-a-bluetooth-le-scanner-for-android-8d27f63d4de9

    // Bluetooth stack
    private lateinit var btManager: BluetoothManager
    private lateinit var bleScanManager: BleScanManager

    // Data in a custom BleDevice to represent a device found by the scanner
    private lateinit var foundDevices: MutableList<BleDevice>

    // UI adapter for list
    private lateinit var adapter: BleDeviceAdapter

    // Views
    private lateinit var btnStartScan: Button

    private lateinit var message: TextView

    // Permissions
    private val permissions = BleScanRequiredPermissions().permissions

    private lateinit var device: ConnectableBleDevice

    //////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // find the RecyclerView for the list
        val rvFoundDevices = this.findViewById<View>(R.id.rv_found_devices) as RecyclerView

        // initialize the list of devices (empty list)
        this.foundDevices = BleDevice.createBleDevicesList()

        // initialize the adapter with the empty list
        this.adapter = BleDeviceAdapter(this.foundDevices, this)

        // set the adapter to the recycler view
        rvFoundDevices.adapter = this.adapter
        rvFoundDevices.layoutManager = LinearLayoutManager(this)

        // init views
        this.btnStartScan = this.findViewById(R.id.button_start_scan)
        this.message = this.findViewById(R.id.textView_message)

        // init bluetooth stack
        this.btManager = this.getSystemService(BluetoothManager::class.java)

        this.btnStartScan.setOnClickListener {
            // Checks if the required permissions are granted and starts the scan if so,
            // otherwise it requests them
            this.launcher.launch(this.permissions)
        }

    }

    //////////////////// Permissions to scan

    val launcher = this.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ActivityResultCallback {
            val permissionsResults = it
            var deniedPermissions = ArrayList<String>()

            this.permissions.forEach {
                val permission = it
                val isGranted = permissionsResults.get(permission)

                if(!isGranted!!) {

                    deniedPermissions.add(permission)
                }
            }

            if(deniedPermissions.isEmpty()) {

                // Run SCAN action if all permissions are granted
                    this.configureBleScanManager(this.btManager)
                this.scan()

            } else {

                // Some permissions have been denied
                this.showOnDeniedPermissionsMessage(deniedPermissions)

            }
        }
    )

    //////////////////////// SCAN

    private fun scan() {

        try {

            this.bleScanManager.scanBleDevices()

        } catch (e: BluetoothDisabledException) {
            this.showOnBluetoothDisabledMessage(e.javaClass.name)
        }
    }

    private fun configureBleScanManager(bluetoothManager: BluetoothManager) {

        this.bleScanManager = BleScanManager(this.btManager, 5000, scanCallBack =
        BleScanCallBack({
            if (it != null) {

                // CREATE BleDevice for the adapter
                // Extract data from scan result to create a custom BleDevice object
                    // that represents the device viewed by the scan
                val bleDevice = GetDeviceFromScanResult().createBleDeviceFromResult(it)

                // add it to the list of devices that the scanner has found
                // and notify adapter
                if(!this.foundDevices.contains(bleDevice)) {
                    this.foundDevices.add(bleDevice)
                    adapter.notifyItemInserted(this.foundDevices.size - 1)
                }
            }

        }))

        // Adding the actions the manager must do before and after scanning
        this.bleScanManager.beforeScanActions.add {
            this.btnStartScan.isEnabled = false
        }

        this.bleScanManager.beforeScanActions.add {
            // Initialize the list of devices found by the scanner
            // and notify adapter
            this.foundDevices.clear()
            adapter.notifyDataSetChanged()
        }

        this.bleScanManager.afterScanActions.add {
            this.btnStartScan.isEnabled = true
        }
    }

    ////////////////////////// Show messages

    private fun showOnDeniedPermissionsMessage(deniedPermissions: List<String>) {

        var message = "Some permissions were not granted, please grant them and try again: "
        deniedPermissions.forEach {
            message += it + " "
        }

        this.message.text = message
    }

    private fun showOnBluetoothDisabledMessage(blueToothDisabledMessage: String) {
        this.message.text = blueToothDisabledMessage
    }

    //////////////// impl Connect interface

    override fun connect(device: ConnectableBleDevice) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestConnectionPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }


    }


    val requestConnectionPermissionLauncher = this.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            this.connectGatt()
        } else {
            Toast.makeText(this@MainActivity, "nothing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectGatt() {
        val gattCallBack = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: BluetoothGatt?,
                status: Int,
                newState: Int
            ) {

                val response = "connection status: " + status + ", new state: " + newState
                Toast.makeText(this@MainActivity, response, Toast.LENGTH_LONG).show()
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this@MainActivity, "nothing", Toast.LENGTH_SHORT).show()
            return
        }
        device.btDevice.connectGatt(this, true, gattCallBack)

    }


}