package com.leandrolcd.barcode_scanner.ui.camera

import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.leandrolcd.barcode_scanner.domain.CameraX
import com.leandrolcd.barcode_scanner.ui.camera.states.CameraUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraX: CameraX,
    private val scanner: BarcodeScanner
): ViewModel() {

    var uiState by mutableStateOf<CameraUiState>(CameraUiState.Scanning)
        private set
    var camera = mutableStateOf(cameraX)
        private set
    var dimensionBox by mutableStateOf(Rect(0,0,0,0))
        private set
    var barCode by mutableStateOf("")


    fun scannerBarcode(imageProxy: ImageProxy) {
        if (uiState is CameraUiState.Scanning) {
            val image = InputImage.fromBitmap(imageProxy.toBitmap(), 0)
            scanner.process(image).addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    uiState = CameraUiState.Loading
                    for (barcode in barcodes) {
                        barcode.boundingBox?.let {
                            dimensionBox = it
                        }
                        val rawValue = barcode.rawValue
                        Log.d("Barras2", "scannerBarcode: $rawValue")
                        uiState = when (barcode.valueType) {
                            Barcode.TYPE_PRODUCT -> {
                                barCode= rawValue.orEmpty()
                                CameraUiState.Success(rawValue)

                            }
                            else -> {
                                barCode = ""
                                CameraUiState.Error("The scanned code is not a product barcode.")
                            }
                        }
                    }

                }
            }
                .addOnFailureListener { e ->
                    barCode = ""
                    uiState = CameraUiState.Error(e.message.orEmpty())
                }
        }
        imageProxy.close()
    }

    fun onRetryAgain() {
        uiState = CameraUiState.Scanning
        dimensionBox = Rect(0,0,0,0)
    }


}