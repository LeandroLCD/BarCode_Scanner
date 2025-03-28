package com.blipblipcode.scanner.domain

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView

interface ICameraRepository {
    fun startCameraPreviewView(recognizerImage: (ImageProxy) -> Unit): PreviewView
    fun takePhoto(onCaptureSuccess: (Bitmap) -> Unit)
}