package com.blipblipcode.scanner.data.core

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BarcodeScannerModule {
    @Singleton
    @Provides
    fun providesBarcode(): BarcodeScanner{
         val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            ).enableAllPotentialBarcodes()
            .build()
        return BarcodeScanning.getClient(options)
    }
}