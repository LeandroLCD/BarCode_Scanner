package com.blipblipcode.scanner.ui.camera.states

sealed class CameraUiState{

    data object Idle:CameraUiState()

    data object Scanning:CameraUiState()

    data class Exception(val cause:Throwable):CameraUiState()

}