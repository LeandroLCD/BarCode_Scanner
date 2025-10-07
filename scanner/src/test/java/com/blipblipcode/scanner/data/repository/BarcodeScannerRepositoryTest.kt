package com.blipblipcode.scanner.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BarcodeScannerRepositoryTest {

    private lateinit var context: Context
    private lateinit var barcodeScanning: BarcodeScanner
    private lateinit var barcodeScannerRepository: BarcodeScannerRepository

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        barcodeScanning = mockk()
        barcodeScannerRepository = BarcodeScannerRepository(context, barcodeScanning)

        // Mock static method for InputImage.fromFilePath
        mockkStatic(InputImage::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(InputImage::class)
    }

    @Test
    fun `scanningBarcode with ImageProxy returns success with barcode`() = runTest {
        /*GIVEN*/
        val mockImageProxy = mockk<ImageProxy>(relaxed = true)
        val mockMediaImage = mockk<android.media.Image>(relaxed = true)
        val mockBarcode = mockk<Barcode>()
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { mockImageProxy.image } returns mockMediaImage
        every { mockImageProxy.imageInfo.rotationDegrees } returns 0
        every { InputImage.fromMediaImage(mockMediaImage, 0) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns listOf(mockBarcode)
        every { mockTask.addOnSuccessListener(any()) } coAnswers {
            firstArg<OnSuccessListener<List<Barcode>>>().onSuccess(listOf(mockBarcode))
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockImageProxy)

        /*THEN*/
        assertTrue(result.isSuccess)
        assertEquals(mockBarcode, result.getOrNull())
        verify(exactly = 1) { mockImageProxy.close() }
    }

    @Test
    fun `scanningBarcode with ImageProxy returns success with null barcode`() = runTest {
        /*GIVEN*/
        val mockImageProxy = mockk<ImageProxy>(relaxed = true)
        val mockMediaImage = mockk<android.media.Image>(relaxed = true)
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { mockImageProxy.image } returns mockMediaImage
        every { mockImageProxy.imageInfo.rotationDegrees } returns 0
        every { InputImage.fromMediaImage(mockMediaImage, 0) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns emptyList()
        every { mockTask.addOnSuccessListener(any()) } coAnswers {
            firstArg<OnSuccessListener<List<Barcode>>>().onSuccess(emptyList())
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockImageProxy)

        /*THEN*/
        assertTrue(result.isSuccess)
        assertFalse(result.isFailure)
        assertEquals(null, result.getOrNull())
        verify(exactly = 1) { mockImageProxy.close() }
    }

    @Test
    fun `scanningBarcode with ImageProxy returns failure`() = runTest {
        /*GIVEN*/
        val mockImageProxy = mockk<ImageProxy>(relaxed = true)
        val mockMediaImage = mockk<android.media.Image>(relaxed = true)
        val exception = RuntimeException("Scanner error")
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { mockImageProxy.image } returns mockMediaImage
        every { mockImageProxy.imageInfo.rotationDegrees } returns 0
        every { InputImage.fromMediaImage(mockMediaImage, 0) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns false
        every { mockTask.exception } returns exception
        every { mockTask.addOnFailureListener(any()) } coAnswers {
            firstArg<OnFailureListener>().onFailure(exception)
            mockTask
        }
        every { mockTask.addOnSuccessListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockImageProxy)

        /*THEN*/
        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
        verify(exactly = 1) { mockImageProxy.close() }
    }

    @Test
    fun `scanningBarcode with Uri returns success with barcode`() = runTest {
        /*GIVEN*/
        val mockUri = mockk<Uri>()
        val mockBarcode = mockk<Barcode>()
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { InputImage.fromFilePath(context, mockUri) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns listOf(mockBarcode)
        every { mockTask.addOnSuccessListener(any()) } coAnswers {
            firstArg<OnSuccessListener<List<Barcode>>>().onSuccess(listOf(mockBarcode))
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockUri)

        /*THEN*/
        assertTrue(result.isSuccess)
        assertEquals(mockBarcode, result.getOrNull())
    }

    @Test
    fun `scanningBarcode with Uri returns success with null barcode`() = runTest {
        /*GIVEN*/
        val mockUri = mockk<Uri>()
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { InputImage.fromFilePath(context, mockUri) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns emptyList()
        every { mockTask.addOnSuccessListener(any()) } coAnswers {
            firstArg<OnSuccessListener<List<Barcode>>>().onSuccess(emptyList())
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockUri)

        /*THEN*/
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun `scanningBarcode with Uri returns failure`() = runTest {
        /*GIVEN*/
        val mockUri = mockk<Uri>()
        val exception = RuntimeException("Scanner error")
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { InputImage.fromFilePath(context, mockUri) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns false
        every { mockTask.exception } returns exception
        every { mockTask.addOnFailureListener(any()) } coAnswers {
            firstArg<OnFailureListener>().onFailure(exception)
            mockTask
        }
        every { mockTask.addOnSuccessListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockUri)

        /*THEN*/
        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `scanningBarcode with Bitmap returns success with barcode`() = runTest {
        /*GIVEN*/
        val mockBitmap = mockk<Bitmap>()
        val rotationDegrees = 0
        val mockBarcode = mockk<Barcode>()
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { InputImage.fromBitmap(mockBitmap, rotationDegrees) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns listOf(mockBarcode)
        every { mockTask.addOnSuccessListener(any()) } coAnswers {
            firstArg<OnSuccessListener<List<Barcode>>>().onSuccess(listOf(mockBarcode))
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockBitmap, rotationDegrees)

        /*THEN*/
        assertTrue(result.isSuccess)
        assertEquals(mockBarcode, result.getOrNull())
    }

    @Test
    fun `scanningBarcode with Bitmap returns success with null barcode`() = runTest {
        /*GIVEN*/
        val mockBitmap = mockk<Bitmap>()
        val rotationDegrees = 0
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { InputImage.fromBitmap(mockBitmap, rotationDegrees) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns emptyList()
        every { mockTask.addOnSuccessListener(any()) } coAnswers {
            firstArg<OnSuccessListener<List<Barcode>>>().onSuccess(emptyList())
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockBitmap, rotationDegrees)

        /*THEN*/
        assertTrue(result.isSuccess)
        assertEquals(null, result.getOrNull())
    }

    @Test
    fun `scanningBarcode with Bitmap returns failure`() = runTest {
        /*GIVEN*/
        val mockBitmap = mockk<Bitmap>()
        val rotationDegrees = 0
        val exception = RuntimeException("Scanner error")
        val mockTask = mockk<Task<List<Barcode>>>()
        val inputImage = mockk<InputImage>()

        every { InputImage.fromBitmap(mockBitmap, rotationDegrees) } returns inputImage
        every { barcodeScanning.process(inputImage) } returns mockTask
        every { mockTask.isSuccessful } returns false
        every { mockTask.exception } returns exception
        every { mockTask.addOnFailureListener(any()) } coAnswers {
            firstArg<OnFailureListener>().onFailure(exception)
            mockTask
        }
        every { mockTask.addOnSuccessListener(any()) } returns mockTask

        /*WHEN*/
        val result = barcodeScannerRepository.scanningBarcode(mockBitmap, rotationDegrees)

        /*THEN*/
        assertTrue(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }
}