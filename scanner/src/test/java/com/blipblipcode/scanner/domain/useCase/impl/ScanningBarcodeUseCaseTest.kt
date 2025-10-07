package com.blipblipcode.scanner.domain.useCase.impl

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.blipblipcode.scanner.data.repository.BarcodeScannerRepository
import com.google.mlkit.vision.barcode.common.Barcode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ScanningBarcodeUseCaseTest {

    private lateinit var barcodeScannerRepository: BarcodeScannerRepository
    private lateinit var scanningBarcodeUseCase: ScanningBarcodeUseCase

    @Before
    fun setUp() {
        barcodeScannerRepository = mockk()
        scanningBarcodeUseCase = ScanningBarcodeUseCase(barcodeScannerRepository)
    }

    @Test
    fun `invoke with ImageProxy returns success with barcode`() = runTest {
        /*GIVEN*/
        val mockImageProxy = mockk<ImageProxy>()
        val mockBarcode = mockk<Barcode>()
        coEvery { barcodeScannerRepository.scanningBarcode(mockImageProxy) } returns Result.success(mockBarcode)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockImageProxy)

        /*THEN*/
        assertEquals(Result.success(mockBarcode), result)
    }

    @Test
    fun `invoke with ImageProxy returns success with null barcode`() = runTest {
        /*GIVEN*/
        val mockImageProxy = mockk<ImageProxy>()
        coEvery { barcodeScannerRepository.scanningBarcode(mockImageProxy) } returns Result.success(null)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockImageProxy)

        /*THEN*/
        assertEquals(Result.success(null), result)
    }

    @Test
    fun `invoke with ImageProxy returns failure`() = runTest {
        /*GIVEN*/
        val mockImageProxy = mockk<ImageProxy>()
        val exception = RuntimeException("Error scanning barcode from ImageProxy")
        coEvery { barcodeScannerRepository.scanningBarcode(mockImageProxy) } returns Result.failure(exception)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockImageProxy)

        /*THEN*/
        assert(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with Uri returns success with barcode`() = runTest {
        /*GIVEN*/
        val mockUri = mockk<Uri>()
        val mockBarcode = mockk<Barcode>()
        coEvery { barcodeScannerRepository.scanningBarcode(mockUri) } returns Result.success(mockBarcode)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockUri)

        /*THEN*/
        assertEquals(Result.success(mockBarcode), result)
    }

    @Test
    fun `invoke with Uri returns success with null barcode`() = runTest {
        /*GIVEN*/
        val mockUri = mockk<Uri>()
        coEvery { barcodeScannerRepository.scanningBarcode(mockUri) } returns Result.success(null)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockUri)

        /*THEN*/
        assertEquals(Result.success(null), result)
    }

    @Test
    fun `invoke with Uri returns failure`() = runTest {
        /*GIVEN*/
        val mockUri = mockk<Uri>()
        val exception = RuntimeException("Error scanning barcode from Uri")
        coEvery { barcodeScannerRepository.scanningBarcode(mockUri) } returns Result.failure(exception)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockUri)

        /*THEN*/
        assert(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with Bitmap and rotation returns success with barcode`() = runTest {
        /*GIVEN*/
        val mockBitmap = mockk<Bitmap>()
        val rotationDegrees = 0
        val mockBarcode = mockk<Barcode>()
        coEvery { barcodeScannerRepository.scanningBarcode(mockBitmap, rotationDegrees) } returns Result.success(mockBarcode)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockBitmap, rotationDegrees)

        /*THEN*/
        assertEquals(Result.success(mockBarcode), result)
    }

    @Test
    fun `invoke with Bitmap and rotation returns success with null barcode`() = runTest {
        /*GIVEN*/
        val mockBitmap = mockk<Bitmap>()
        val rotationDegrees = 0
        coEvery { barcodeScannerRepository.scanningBarcode(mockBitmap, rotationDegrees) } returns Result.success(null)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockBitmap, rotationDegrees)

        /*THEN*/
        assertEquals(Result.success(null), result)
    }

    @Test
    fun `invoke with Bitmap and rotation returns failure`() = runTest {
        /*GIVEN*/
        val mockBitmap = mockk<Bitmap>()
        val rotationDegrees = 0
        val exception = RuntimeException("Error scanning barcode from Bitmap")
        coEvery { barcodeScannerRepository.scanningBarcode(mockBitmap, rotationDegrees) } returns Result.failure(exception)

        /*WHEN*/
        val result = scanningBarcodeUseCase.invoke(mockBitmap, rotationDegrees)

        /*THEN*/
        assert(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }
}