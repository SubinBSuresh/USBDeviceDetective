package com.dutch.usbdevicedetective.adapter

import android.hardware.usb.UsbDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dutch.usbdevicedetective.R
import com.dutch.usbdevicedetective.UsbDeviceDetectiveApplication.Companion.LOG_TAG

class UsbDeviceListAdapter(private val onClick: (UsbDevice) -> Unit) :
    ListAdapter<UsbDevice, UsbDeviceListAdapter.ViewHolder>(DiffCallback()) {
    val TAG = LOG_TAG + "UsbDeviceListAdapter"

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val deviceName: TextView = view.findViewById(R.id.tv_deviceName)
        val productId: TextView = view.findViewById(R.id.tv_productId)
        val vendorId: TextView = view.findViewById(R.id.tv_vendorId)
        val deviceInterface: TextView = view.findViewById(R.id.tv_deviceInterface)
        val serialNumber: TextView = view.findViewById(R.id.tv_serialNumber)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.devicelist_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        val deviceData = getItem(position)
        holder.deviceName.text = deviceData.manufacturerName + deviceData.productName
        holder.vendorId.text = deviceData.vendorId.toString()
        holder.productId.text = deviceData.productId.toString()
        holder.deviceInterface.text = deviceData.interfaceCount.toString()
        holder.serialNumber.text = deviceData.deviceName
        holder.itemView.setOnClickListener {
            onClick(deviceData)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UsbDevice>() {
        override fun areItemsTheSame(oldItem: UsbDevice, newItem: UsbDevice): Boolean {
            Log.i(LOG_TAG + "DiffCallback", "areItemsTheSame")
            return oldItem.deviceId == newItem.deviceId
        }

        override fun areContentsTheSame(oldItem: UsbDevice, newItem: UsbDevice): Boolean {
            Log.i(LOG_TAG + "DiffCallback", "areContentsTheSame")
            return oldItem == newItem
        }

    }
}