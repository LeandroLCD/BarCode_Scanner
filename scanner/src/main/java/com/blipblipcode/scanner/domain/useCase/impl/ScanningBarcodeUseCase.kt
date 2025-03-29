package com.blipblipcode.scanner.domain.useCase.impl

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.blipblipcode.scanner.data.repository.BarcodeScannerRepository
import com.blipblipcode.scanner.domain.useCase.IScanningBarcodeUseCase
import com.google.mlkit.vision.barcode.common.Barcode
import javax.inject.Inject

internal class ScanningBarcodeUseCase @Inject constructor(private val barcodeScannerRepository: BarcodeScannerRepository) :
    IScanningBarcodeUseCase {
    override suspend fun invoke(imageProxy: ImageProxy): Result<Barcode?> =
        barcodeScannerRepository.scanningBarcode(imageProxy)

    override suspend fun invoke(url: Uri): Result<Barcode?> =
        barcodeScannerRepository.scanningBarcode(url)

    override suspend fun invoke(bitmap: Bitmap, rotationDegrees: Int): Result<Barcode?> =
        barcodeScannerRepository.scanningBarcode(bitmap, rotationDegrees)
}