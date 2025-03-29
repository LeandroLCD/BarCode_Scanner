package com.blipblipcode.scanner.domain.useCase

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.common.Barcode

interface IScanningBarcodeUseCase {
    suspend operator fun invoke(imageProxy: ImageProxy):Result<Barcode?>
    suspend operator fun invoke(url: android.net.Uri):Result<Barcode?>
    suspend operator fun invoke(bitmap: Bitmap, rotationDegrees: Int):Result<Barcode?>
}