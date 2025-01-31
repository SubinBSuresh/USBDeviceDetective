package com.dutch.usbdevicedetective.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dutch.usbdevicedetective.R
import com.dutch.usbdevicedetective.UsbDeviceDetectiveApplication.Companion.LOG_TAG
import com.dutch.usbdevicedetective.adapter.UsbDeviceListAdapter
import com.dutch.usbdevicedetective.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val TAG = LOG_TAG + "MainActivity"

    private val REQUEST_CODE_STORAGE = 101
    lateinit var mainActivityMainBinding: ActivityMainBinding
    lateinit var usbManager: UsbManager
    lateinit var usbDeviceListAdapter: UsbDeviceListAdapter
    lateinit var tvDump: TextView

    private val usbBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            tvDump.text = intent.toString()
            Log.i(TAG, "onReceiver(), intent: $intent")
            if (intent?.action == UsbManager.ACTION_USB_ACCESSORY_ATTACHED || intent?.action == UsbManager.ACTION_USB_ACCESSORY_DETACHED || intent?.action == UsbManager.ACTION_USB_DEVICE_DETACHED || intent?.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                Log.i(TAG, "usb intents received, so refresh list")
                refreshUsbList()
            } else {
                Log.i(TAG, "some other shitty intents, not doing anything")
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        usbManager = getSystemService(USB_SERVICE) as UsbManager


        mainActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        tvDump = mainActivityMainBinding.tvJunkdump
        setContentView(mainActivityMainBinding.root)

        // set up recyclerView
        setupRecyclerViewContents()

        refreshUsbList()
        mainActivityMainBinding.fabManualRefresh.setOnClickListener {
            Log.i(TAG, "fab onClick()")
            refreshUsbList()
        }


    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy(), unregistering intents")
        unregisterReceiver(usbBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume(), registering intents")
        registerForUsbEvents()
    }


    private fun refreshUsbList() {
        val devices = usbManager.deviceList.values.toList()
        Log.i(TAG, "refreshUsbList$devices")
//        tvDump.text = devices.toList().toString()
        usbDeviceListAdapter.submitList(devices)
    }

    private fun setupRecyclerViewContents() {
        Log.i(TAG, "setupRecyclerViewContents")
        usbDeviceListAdapter = UsbDeviceListAdapter { device ->
            handleDeviceClick(device)
        }
        mainActivityMainBinding.rvDeviceList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = usbDeviceListAdapter
        }

    }

    private fun handleDeviceClick(device: UsbDevice) {
        Log.i(TAG, "handleDeviceClick()")
        val isStorageDevice = (0 until device.interfaceCount).any { index ->
            val usbInterface = device.getInterface(index)
            usbInterface.interfaceClass == UsbConstants.USB_CLASS_MASS_STORAGE
        }
        if (isStorageDevice) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            storageLauncher.launch(intent)
        } else {
            Log.e(TAG, "non storage device")
            Toast.makeText(this, "Not a storage device", Toast.LENGTH_SHORT).show()
        }

    }


    val storageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i(TAG, "registerForActivityResult()")
            if (result.resultCode == RESULT_OK) {
                Log.i(TAG, "result ok")
                result.data?.data?.let { uri ->
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, FileListFragment().createInstance(uri))
                        .addToBackStack(null).commit()
                }
            } else {
                Log.e(TAG, "no permission")
            }
        }

    private fun registerForUsbEvents() {
        Log.i(TAG, "registerForUsbEvents()")
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
            addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED)
        }
        registerReceiver(usbBroadcastReceiver, filter)
    }
}