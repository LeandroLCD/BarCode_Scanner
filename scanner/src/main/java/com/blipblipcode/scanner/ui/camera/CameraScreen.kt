package com.blipblipcode.scanner.ui.camera

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blipblipcode.scanner.ui.camera.states.CameraUiState
import com.blipblipcode.scanner.ui.component.CameraXScreen

@SuppressLint("MissingPermission")
@Composable
internal fun CameraScreen(modifier: Modifier = Modifier,
                          viewModel: CameraViewModel = hiltViewModel(),
                          onClosed: () -> Unit,
                          onException: (Throwable) -> Unit,
                          onComplete: (String, String) -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val preview by viewModel.preview.collectAsStateWithLifecycle()
    var barcode by remember {
        mutableStateOf("")
    }
    var barcodeType by remember {
        mutableStateOf("")
    }


    when(val ue = uiState){
        is CameraUiState.Exception -> onException(ue.cause)
        is CameraUiState.Scanning ->{
            AnimatedVisibility(preview != null) {
                CameraXScreen(preview!!, modifier, onClosed = {
                    IconButton(onClick = {
                        onClosed.invoke()
                    }){
                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }) {
                    AnimatedVisibility(barcode.isNotBlank()) {
                        TextButton(onClick = {
                            onComplete.invoke(barcode, barcodeType)
                        }, colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )){
                            Text(barcode)
                        }
                    }
                }
            }
        }
        else -> {
            LaunchedEffect(Unit){
                viewModel.startCamera{br, type->
                    if(br.isNotBlank()){
                        barcode = br
                        barcodeType = type
                    }
                }
            }
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }

        }
    }
}