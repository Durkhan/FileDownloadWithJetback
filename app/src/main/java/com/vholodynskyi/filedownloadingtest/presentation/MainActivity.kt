package com.vholodynskyi.filedownloadingtest.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.vholodynskyi.filedownloadingtest.presentation.downloading.DownloadingScreen
import com.vholodynskyi.filedownloadingtest.presentation.ui.theme.FileDownloadingTestTheme
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : ComponentActivity(),EasyPermissions.PermissionCallbacks{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileDownloadingTestTheme {
                Surface(color = MaterialTheme.colors.background) {
                    requestPermisssions()
                    DownloadingScreen(this)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms))
            AppSettingsDialog.Builder(this).build().show()
        else
            requestPermisssions()

    }

    private fun requestPermisssions() {
        if (EasyPermissions.hasPermissions(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
            return
        EasyPermissions.requestPermissions(
            this,
            "You need to accept storage permission to use this app",
            101,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    }
}
