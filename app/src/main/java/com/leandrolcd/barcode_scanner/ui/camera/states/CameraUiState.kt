package com.leandrolcd.barcode_scanner.ui.camera.states

sealed class CameraUiState{

    class Success(val data:Any?):CameraUiState()

    class Error(val message:String):CameraUiState()

    object Scanning:CameraUiState()

    object Loading:CameraUiState()

}
