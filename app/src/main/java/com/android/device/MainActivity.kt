package com.android.device

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.device.businesslogic.gateways.FakeListOfBleDevices
import com.android.device.permissions.BleScanRequiredPermissions
import com.android.device.permissions.PermissionsUtilities
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
            this.launcher.launch(permissions)
            this.requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH)
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
                    //this.configureBleScanManager(this.btManager)
                //this.scan()

            } else {

                // Some permissions have been denied
                this.showOnDeniedPermissionsMessage(deniedPermissions)

            }
        }
    )

    // Blootooth permission
    // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher. You can use either a val, as shown in this snippet,
// or a lateinit var in your onAttach() or onCreate() method.
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.

                // configure scan feature
                // implements the scan call back showing the devices that the scan has found
                this.configureBleScanManager(this.btManager)
                this.scan()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                this.showOnDeniedPermissionsMessage(Arrays.asList("permission denied"))

            }
        }

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



    @SuppressLint("MissingPermission")
    override fun connect(address: String) {
        val btAdapter = this.btManager.adapter
        val btDevice = btAdapter.getRemoteDevice(address)
        // Connect to GATT Server hosted by this device
        btDevice.connectGatt(this, false, ConnectCallBack())
    }



}