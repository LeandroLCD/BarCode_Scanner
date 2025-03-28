package com.blipblipcode.scanner.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.blipblipcode.scanner.domain.IBarcodeScannerRepository
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

internal class BarcodeScannerRepository @Inject constructor(
    private var context: Context,
    private val barcodeScanning: BarcodeScanner
) : IBarcodeScannerRepository {
    @OptIn(ExperimentalGetImage::class)
    override suspend fun scanningBarcode(imageProxy: ImageProxy):Result<Barcode>{
        val image = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        val result = scanningBarcode(image)
        imageProxy.close()
        return result
    }

    override suspend fun scanningBarcode(url: android.net.Uri):Result<Barcode>{
        val image = InputImage.fromFilePath(context, url)
        return scanningBarcode(image)
    }

    override suspend fun scanningBarcode(bitmap: Bitmap, rotationDegrees: Int):Result<Barcode>{
        val image = InputImage.fromBitmap(bitmap, rotationDegrees)
        return scanningBarcode(image)
    }

    private suspend fun scanningBarcode(image: InputImage):Result<Barcode>{
        return suspendCancellableCoroutine {continuation->
            barcodeScanning.process(image).addOnSuccessListener { barcodes->
                barcodes.firstOrNull()?.let {br->
                    continuation.resume(Result.success(br))
                }
            }.addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
        }
    }
}