package com.leandrolcd.barcode_scanner.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.leandrolcd.barcode_scanner.ui.camera.states.CameraUiState
import com.leandrolcd.barcode_scanner.ui.camera.utils.Commons.REQUIRED_PERMISSIONS
import kotlinx.coroutines.delay


@Composable
fun CameraXScreen(viewModel: CameraViewModel = hiltViewModel()) {


    //region Permission Cam
    val context = LocalContext.current
    var hasCamPermission by remember {
        mutableStateOf(
            REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(context, it) ==
                        PackageManager.PERMISSION_GRANTED
            })
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { granted ->
            hasCamPermission = granted.size == 2
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }
    //endregion
    if (hasCamPermission) {
        when (val state = viewModel.uiState) {
            is CameraUiState.Error -> ErrorCameraScreen(state.message) {
                viewModel.onRetryAgain()
            }
            else -> {
                StartCamera(viewModel)
            }
        }
    }

}

@Composable
fun ErrorCameraScreen(message: String, onRetryAgain: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message)
        Button(onClick = { onRetryAgain() }) {
            Text(text = "Retry")
        }
    }

}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun StartCamera(viewModel: CameraViewModel) {


    Surface(modifier = Modifier.fillMaxSize()) {
        Box() {


            Box(Modifier.fillMaxSize()) {
                AndroidView(modifier = Modifier.fillMaxSize(),
                    factory = {
                        viewModel.camera.value.startBarcodeScannerPreviewView() {
                            viewModel.scannerBarcode(it)
                        }
                    }
                )
                MyScanner()

            }
            if (viewModel.uiState is CameraUiState.Success) {
                Row(Modifier
                    .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {

                        Text(
                            text = viewModel.barCode,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )}

                    Button(onClick = { viewModel.onRetryAgain() },
                        modifier = Modifier.size(60.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp,
                            pressedElevation = 0.dp)) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "",
                            tint = Color.Black
                        )
                    }
                }

            }
        }


    }
}

@Preview(showBackground = true, device = Devices.NEXUS_5, backgroundColor = 0xFFFFFFFF)
@Composable
fun MyScanner() {
    var showLine by remember { mutableStateOf(true) } // variable que indica si se debe mostrar la línea o no

    LaunchedEffect(true) {
        while (true) {
            showLine = !showLine
            delay(500)
        }
    }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }) {
        drawRect(color = Color.Black.copy(0.7f))
        val width = size.height * 0.3f
        val height = size.width * 0.3f
        val cord = Offset((size.width - width) / 2f, (size.height - height) / 2f)
        drawRect(
            color = Color.Transparent,
            topLeft = cord,
            size = Size(width, height),
            blendMode = BlendMode.Clear,
        )
        val path = Path().apply {
            moveTo(cord.x, cord.y)
            lineTo(cord.x, cord.y + height)
            lineTo(cord.x + width, cord.y + height)
            lineTo(cord.x + width, cord.y)
            close()
        }
        drawPath(path, Color.Black, style = Stroke(width = 3f))

        if (showLine) {
            drawLine(
                color = Color.Black,
                strokeWidth = 1f,
                start = Offset((size.width - width) / 2f, (size.height) / 2f),
                end = Offset((size.width + width) / 2f, (size.height) / 2f)
            )
        }
    }
}


@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        this@toPx.toPx()
    }
}