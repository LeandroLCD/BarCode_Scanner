package com.blipblipcode.scanner.domain

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.common.Barcode

interface IBarcodeScannerRepository {
    suspend fun scanningBarcode(imageProxy: ImageProxy): Result<Barcode>
    suspend fun scanningBarcode(url: Uri): Result<Barcode>
    suspend fun scanningBarcode(bitmap: Bitmap, rotationDegrees: Int): Result<Barcode>
}