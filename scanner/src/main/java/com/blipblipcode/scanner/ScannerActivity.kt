package com.blipblipcode.scanner

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blipblipcode.scanner.ui.camera.CameraScreen
import com.blipblipcode.scanner.ui.camera.CameraViewModel
import com.blipblipcode.scanner.ui.component.ErrorSnackBar
import com.blipblipcode.scanner.ui.permission.PermissionManager
import com.blipblipcode.scanner.ui.permission.PermissionsScreen
import com.blipblipcode.scanner.ui.theme.BarCode_ScannerTheme
import com.blipblipcode.scanner.ui.utilities.EXTRA_BARCODE
import com.blipblipcode.scanner.ui.utilities.EXTRA_BARCODE_TYPE
import com.blipblipcode.scanner.ui.utilities.EXTRA_ERROR
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager
    private val resultIntent = Intent()
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionManager = PermissionManager
            .Builder()
            .addPermission(permission.CAMERA)
            .build(this)

        setContent {
            val viewModel: CameraViewModel by viewModels<CameraViewModel>()
            BarCode_ScannerTheme {
                val permissionStates = rememberSaveable {
                    permissionManager.getPermissionStates()
                }
                var permissionGranted by remember {
                    mutableStateOf(permissionStates.all { it.isGranted })
                }
                val snackbarHostState = remember {
                    SnackbarHostState()
                }
                val errorException by viewModel.errorException.collectAsStateWithLifecycle()

                LaunchedEffect(errorException) {
                    errorException?.let {
                        snackbarHostState.showSnackbar(it.message.orEmpty())
                        it.printStackTrace()
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        ErrorSnackBar(snackbarHostState)
                    }){ innerPadding ->
                    Box(Modifier
                        .fillMaxSize()
                        .padding(innerPadding)) {
                        if (permissionGranted) {

                            CameraScreen(
                                viewModel = viewModel,
                                onClosed = {
                                    setResult(RESULT_CANCELED)
                                    finish()
                                },
                                onException = {
                                resultIntent.putExtra(EXTRA_ERROR, it.message)
                                setResult(RESULT_CANCELED, resultIntent)
                                finish()
                            }) {barcode, type->
                                resultIntent.run {
                                    putExtra(EXTRA_BARCODE, barcode)
                                    putExtra(EXTRA_BARCODE_TYPE, type)
                                }
                                setResult(RESULT_OK, resultIntent)
                                finish()
                            }
                        }else{
                            PermissionsScreen(permissionManager){
                                permissionGranted = true
                            }
                        }


                    }
                }
            }
        }
    }
}
