package de.lulebe.vakation.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat


object Permissions {
    val needed = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    val possible = emptyArray<String>()
    fun needsPermissions (context: Context): Boolean {
        return needed.any {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }
}