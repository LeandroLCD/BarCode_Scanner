package com.blipblipcode.scanner.ui.permission

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.blipblipcode.scanner.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsScreen(permissionManager: PermissionManager, onPermissionGranted: () -> Unit) {
    val context = LocalActivity.current
    var isPermanentlyDenied by remember {
        mutableStateOf(false)
    }
    val permissionState = permissionManager.rememberMultiplePermissionsState { respond ->
        if (respond.any { it.isGranted }) {
            onPermissionGranted.invoke()
        } else {
            isPermanentlyDenied = respond.any { it.isPermanentlyDenied }
        }
    }


    Scaffold { innerPadding ->
        val currentLifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
        DisposableEffect(Unit) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    if (permissionManager.updatePermissionStates().getPermissionStates().any { it.isGranted }) {
                        onPermissionGranted.invoke()
                    }
                }
            }
            currentLifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                currentLifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Surface(
            Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(15.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.no_cam_perms),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(20.dp)
                )



                Button(onClick = {
                    if (isPermanentlyDenied) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context?.packageName, null)
                        }
                        context?.startActivity(intent)
                    } else {
                        permissionState.launchMultiplePermissionRequest()
                    }
                }, shape = RoundedCornerShape(4.dp)) {
                    Text(stringResource(R.string.ask_perms))
                }
                Spacer(Modifier.height(8.dp))

            }
        }
    }
}