package com.blipblipcode.scanner.ui.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Parcelable
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PermissionState(
    val perm: String,
    var isGranted: Boolean = false,
    var shouldShowRequestPermissionRationale: Boolean = true
) : Parcelable{
    fun checkedRequestPermission(context: Activity) {
        if (!isGranted) {
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(context, perm)
        }
    }
}

class PermissionManager private constructor(
    private val permissions: List<String>
) {
    private val permissionStates = mutableListOf<PermissionState>()

    private lateinit var context: Context

    fun build(activity: Activity) = apply {
        this.context = activity
    }

    fun checkedPermissions():List<PermissionState>{
        permissionStates.clear()

        permissionStates.addAll(
            permissions.map { perm ->
                val isGranted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
                val shouldShowRationale = shouldShowRequestPermissionRationale(context, perm)
                PermissionState(perm, isGranted, shouldShowRationale)
            }
        )
        return permissionStates
    }


    private fun shouldShowRequestPermissionRationale(context: Context, perm: String): Boolean {
        return context is Activity && context.shouldShowRequestPermissionRationale(perm)
    }

    @Suppress("unused")
    class Builder {
        private val permissions = mutableSetOf<String>()

        fun add(permission: String) = apply {
            permissions.add(permission)
        }

        fun addAll(vararg permissionList: String) = apply {
            permissions.addAll(permissionList)
        }

        fun build(context: Activity):PermissionManager{
           return PermissionManager(permissions.toList()).build(context)
        }
    }
}