package com.dutch.usbdevicedetective.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dutch.usbdevicedetective.UsbDeviceDetectiveApplication.Companion.LOG_TAG
import com.dutch.usbdevicedetective.adapter.FileListAdapter
import com.dutch.usbdevicedetective.databinding.FragmentFileListBinding

class FileListFragment : Fragment() {
    private val TAG = LOG_TAG + "FileListFragment"
    private lateinit var fileListFragmentBinding: FragmentFileListBinding
    private lateinit var rootUri: Uri
    private val uri_key = "uri"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView()")
        // Inflate the layout for this fragment
        fileListFragmentBinding = FragmentFileListBinding.inflate(inflater, container, false)
        rootUri = arguments?.getParcelable(uri_key) ?: return fileListFragmentBinding.root

        val rootDoc = DocumentFile.fromTreeUri(requireContext(), rootUri)
        val files = rootDoc?.listFiles()?.toList() ?: emptyList()
        val adapter = FileListAdapter(files)
        fileListFragmentBinding.rvFileList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        return fileListFragmentBinding.root
    }

    fun createInstance(uri: Uri): FileListFragment {
        Log.i(TAG, "createInstance()")
        return FileListFragment().apply {
            arguments = Bundle().apply {
                putParcelable(uri_key, uri)
            }
        }
    }


}