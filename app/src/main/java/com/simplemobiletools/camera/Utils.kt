package com.simplemobiletools.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Point
import android.hardware.Camera
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.simplemobiletools.camera.extensions.getFileDocument
import com.simplemobiletools.camera.extensions.isKitkat
import com.simplemobiletools.camera.extensions.isPathOnSD
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        fun getCameraInfo(cameraId: Int): Camera.CameraInfo {
            val info = android.hardware.Camera.CameraInfo()
            Camera.getCameraInfo(cameraId, info)
            return info
        }

        fun showToast(context: Context, resId: Int) {
            Toast.makeText(context, context.resources.getString(resId), Toast.LENGTH_SHORT).show()
        }

        fun hasFlash(camera: Camera?): Boolean {
            if (camera == null) {
                return false
            }

            val parameters = camera.parameters

            if (parameters.flashMode == null) {
                return false
            }

            val supportedFlashModes = parameters.supportedFlashModes
            if (supportedFlashModes == null || supportedFlashModes.isEmpty() ||
                    supportedFlashModes.size == 1 && supportedFlashModes[0] == Camera.Parameters.FLASH_MODE_OFF) {
                return false
            }

            return true
        }

        fun getOutputMediaFile(context: Context, isPhoto: Boolean): String {
            val mediaStorageDir = File(Config.newInstance(context).savePhotosFolder)

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return ""
                }
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            if (isPhoto) {
                return mediaStorageDir.path + File.separator + "IMG_" + timestamp + ".jpg"
            } else {
                return mediaStorageDir.path + File.separator + "VID_" + timestamp + ".mp4"
            }
        }

        fun formatSeconds(duration: Int): String {
            val sb = StringBuilder(8)
            val hours = duration / (60 * 60)
            val minutes = duration % (60 * 60) / 60
            val seconds = duration % (60 * 60) % 60

            if (duration > 3600000) {
                sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
            }

            sb.append(String.format(Locale.getDefault(), "%02d", minutes))
            sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))

            return sb.toString()
        }

        fun getScreenSize(activity: Activity): Point {
            val display = activity.windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.y += getNavBarHeight(activity.resources)
            return size
        }

        fun getNavBarHeight(res: Resources): Int {
            val id = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (id > 0 && hasNavBar(res)) {
                return res.getDimensionPixelSize(id)
            }

            return 0
        }

        fun hasNavBar(res: Resources): Boolean {
            val id = res.getIdentifier("config_showNavigationBar", "bool", "android")
            return id > 0 && res.getBoolean(id)
        }

        fun hasCameraPermission(cxt: Context): Boolean {
            return ContextCompat.checkSelfPermission(cxt, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }

        fun hasStoragePermission(cxt: Context): Boolean {
            return ContextCompat.checkSelfPermission(cxt, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        fun hasAudioPermission(cxt: Context): Boolean {
            return ContextCompat.checkSelfPermission(cxt, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        }

        fun needsStupidWritePermissions(context: Context, path: String) = context.isPathOnSD(path) && context.isKitkat()

        fun getFileDocument(context: Context, path: String) = context.getFileDocument(path)
    }
}
