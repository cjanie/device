package com.android.device

import android.bluetooth.BluetoothManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.device.permissions.BleScanRequiredPermissions
import com.android.device.permissions.PermissionsUtilities
import com.android.device.scanner.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    // https://medium.com/geekculture/how-to-create-a-bluetooth-le-scanner-for-android-8d27f63d4de9

    private lateinit var btManager: BluetoothManager
    private lateinit var bleScanManager: BleScanManager

    private lateinit var foundDevices: MutableList<BleDevice>
    private lateinit var adapter: BleDeviceAdapter

    private lateinit var btnStartScan: Button

    private lateinit var message: TextView

    private val permissions = BleScanRequiredPermissions().permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView handling
        val rvFoundDevices = this.findViewById<View>(R.id.rv_found_devices) as RecyclerView
        this.foundDevices = BleDevice.createBleDevicesList()
        this.adapter = BleDeviceAdapter(this.foundDevices)
        rvFoundDevices.adapter = this.adapter
        rvFoundDevices.layoutManager = LinearLayoutManager(this)

        this.btnStartScan = this.findViewById(R.id.button_start_scan)
        this.message = this.findViewById(R.id.textView_message)

        this.configureBleScanManager()

        this.btnStartScan.setOnClickListener {
            // Checks if the required permissions are granted and starts the scan if so, otherwise it requests them
            this.launcher.launch(permissions)
        }

    }

    private fun configureBleScanManager() {
        this.btManager = this.getSystemService(BluetoothManager::class.java)
        this.bleScanManager = BleScanManager(this.btManager, 5000, scanCallBack =
        BleScanCallBack({
            val name = it?.device?.address
            if(name.isNullOrBlank()) return@BleScanCallBack

            val device = BleDevice(name)
            if(!this.foundDevices.contains(device)) {
                this.foundDevices.add(device)
                adapter.notifyItemInserted(this.foundDevices.size - 1)
            }
        }))

        // Adding the actions the manager must do before and after scanning
        this.bleScanManager.beforeScanActions.add {
            this.btnStartScan.isEnabled = false
        }

        this.bleScanManager.beforeScanActions.add {
            this.foundDevices.clear()
            adapter.notifyDataSetChanged()
        }

        this.bleScanManager.afterScanActions.add {
            this.btnStartScan.isEnabled = true
        }

    }

    val launcher = this.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ActivityResultCallback {
            val permissionsResults = it
            var missingPermissions = ArrayList<String>()

            this.permissions.forEach {
                val permission = it
                val isGranted = permissionsResults.get(permission)
                if(!isGranted!!) {
                    missingPermissions.add(permission)
                }
            }

            if(missingPermissions.isEmpty()) {
                this.configureBleScanManager()
                try {
                    this.bleScanManager.scanBleDevices()
                } catch (e: BluetoothDisabledException) {
                    this.message.text = e.javaClass.name
                }
            } else {
                var message = "Some permissions were not granted, please grant them and try again: "
                missingPermissions.forEach {
                    message += it + " "
                }

                this.message.text = message

                Toast.makeText(
                    this,
                     message,
                    Toast.LENGTH_LONG).show()
            }
        }
    )
/*
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionsUtilities().dispatchOnRequestPermissionsResult(
            requestCode,
            grantResults,
            onGrantedMap = mapOf(BLE_PERMISSION_REQUEST_CODE to {
                this.bleScanManager.scanBleDevices()
            }),
            onDeniedMap = mapOf(BLE_PERMISSION_REQUEST_CODE to {
                Toast.makeText(
                    this,
                    "Some permissions were not granted, please grant them and try again",
                    Toast.LENGTH_LONG).show()
            })
        )
    }

 */

    companion object {
        private const val BLE_PERMISSION_REQUEST_CODE = 1
    }
}