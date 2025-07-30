package com.blipblipcode.scanner.ui.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.parcelize.Parcelize
import java.lang.ref.WeakReference

@Parcelize
data class PermissionState(
    val perm: String,
    val isGranted: Boolean,
    val shouldShowRequestPermissionRationale: Boolean,
    val isPermanentlyDenied: Boolean = !isGranted && !shouldShowRequestPermissionRationale
) : Parcelable


class PermissionManager private constructor(
    private val permissions: List<String>
) {
    private val permissionStates = mutableMapOf<String, PermissionState>()
    private var contextRef: WeakReference<Activity>? = null

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun rememberMultiplePermissionsState(callBack:(List<PermissionState>)->Unit) =
        com.google.accompanist.permissions.rememberMultiplePermissionsState(permissions) { mapPerm ->
            mapPerm.forEach {
                updatePermissionStates(it.key, it.value)
            }
            callBack.invoke(permissionStates.values.toList())
        }

    fun build(activity: Activity) = apply {
        contextRef = WeakReference(activity)
        initializeStates()
    }

    fun updatePermissionStates() =apply {
        val context = contextRef?.get()
        permissionStates.keys.forEach { perm ->
            permissionStates[perm] = PermissionState(
                perm = perm,
                isGranted = checkPermissionStatus(perm, context),
                shouldShowRequestPermissionRationale = checkRationale(perm, context)
            )
        }
    }
    fun getPermissionStates(): List<PermissionState> {
        return permissionStates.values.toList()
    }

    fun getPermissionState(permission: String): PermissionState? {
        return permissionStates[permission]
    }

    private fun initializeStates() {
        val context = contextRef?.get() ?: return
        permissions.forEach { perm ->
            permissionStates[perm] = createPermissionState(perm, context)
        }
    }

    private fun updatePermissionStates(perm: String, isGranted: Boolean) {
        permissionStates[perm] = PermissionState(
            perm = perm,
            isGranted = isGranted,
            shouldShowRequestPermissionRationale = checkRationale(perm, contextRef?.get())
        )
    }

    private fun createPermissionState(perm: String, context: Activity): PermissionState {
        return PermissionState(
            perm = perm,
            isGranted = checkPermissionStatus(perm, context),
            shouldShowRequestPermissionRationale = checkRationale(perm, context)
        )
    }

    private fun checkPermissionStatus(perm: String,  context: Activity?): Boolean {
        return context?.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkRationale(perm: String, context: Activity?): Boolean {
        return context?.shouldShowRequestPermissionRationale(perm) ?: true
    }

    class Builder {
        private val permissions = mutableSetOf<String>()

        fun addPermission(permission: String) = apply {
            permissions.add(permission)
        }

        fun addAllPermissions(vararg permissions: String) = apply {
            this.permissions.addAll(permissions)
        }

        fun build(activity: Activity): PermissionManager {
            return PermissionManager(permissions.toList()).build(activity)
        }
    }
}