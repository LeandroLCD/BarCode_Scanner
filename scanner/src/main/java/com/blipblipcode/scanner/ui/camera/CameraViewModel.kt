package com.blipblipcode.scanner.ui.camera

import androidx.camera.view.PreviewView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blipblipcode.scanner.domain.useCase.IScanningBarcodeUseCase
import com.blipblipcode.scanner.domain.useCase.IStartCameraUseCase
import com.blipblipcode.scanner.ui.camera.states.CameraUiState
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val scanningBarcodeUseCase: dagger.Lazy<IScanningBarcodeUseCase>,
    private val startCameraUseCase: dagger.Lazy<IStartCameraUseCase>
) : ViewModel() {

    /*STATES*/
    private val _preview = MutableStateFlow<PreviewView?>(null)
    val preview = _preview.asStateFlow()

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _errorException:MutableStateFlow<Throwable?> = MutableStateFlow(null)
    val errorException = _errorException.asStateFlow()

    /*EVENT*/
    fun startCamera(onComplete: (String, String) -> Unit) {
        startCameraUseCase.get().invoke { imageProxy ->
            viewModelScope.launch {
                scanningBarcodeUseCase.get().invoke(imageProxy)
                    .onSuccess {br->
                        br?.let{
                            onComplete(it.displayValue.orEmpty(), getType(it.format))
                        }

                    }.onFailure {
                        _errorException.tryEmit(it)
                    }
            }
        }.onSuccess { previewView ->
            _preview.tryEmit(previewView)
            _uiState.tryEmit(CameraUiState.Scanning )
        }.onFailure {
            _uiState.tryEmit(CameraUiState.Exception(it))
        }
    }

    private fun getType(format: Int): String {
      when(format){
            Barcode.FORMAT_QR_CODE -> return "QR Code"
            Barcode.FORMAT_AZTEC -> return "Aztec"
            Barcode.FORMAT_CODABAR -> return "Codabar"
            Barcode.FORMAT_CODE_39 -> return "Code 39"
            Barcode.FORMAT_CODE_93 -> return "Code 93"
            Barcode.FORMAT_CODE_128 -> return "Code 128"
            Barcode.FORMAT_DATA_MATRIX -> return "Data Matrix"
            Barcode.FORMAT_EAN_13 -> return "EAN 13"
            Barcode.FORMAT_EAN_8 -> return "EAN 8"
            Barcode.FORMAT_ITF -> return "ITF"
            Barcode.FORMAT_PDF417 -> return "PDF 417"
            Barcode.FORMAT_UPC_A -> return "UPC A"
            Barcode.FORMAT_UPC_E -> return "UPC E"
            else -> return "Unknown"
        }
    }

}