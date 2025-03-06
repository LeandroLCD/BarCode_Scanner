package com.leandrolcd.barcode_scanner.core.camera

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BarcodeModule {
    @Provides
    fun provideBarcode(): BarcodeScanner {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS)
            .enableAllPotentialBarcodes()
            .build()
        return BarcodeScanning.getClient(options)
    }

}