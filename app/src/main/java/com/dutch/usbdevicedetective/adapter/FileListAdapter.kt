package com.dutch.usbdevicedetective.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.dutch.usbdevicedetective.R
import com.dutch.usbdevicedetective.UsbDeviceDetectiveApplication.Companion.LOG_TAG

class FileListAdapter(private val files: List<DocumentFile>) :
    RecyclerView.Adapter<FileListAdapter.ViewHolder>() {
    private val TAG = LOG_TAG + "FileListAdapter"

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView = view.findViewById(R.id.tv_fileName)
        val fileType: TextView = view.findViewById(R.id.tv_fileType)
        val fileSize: TextView = view.findViewById(R.id.tv_fileSize)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.filelist_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.i(TAG, "getItemCount()")
        return files.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")
        val file = files[position]
        holder.fileName.text = file.name
        holder.fileSize.text = file.parentFile.toString()
        holder.fileType.text = file.type
    }
}