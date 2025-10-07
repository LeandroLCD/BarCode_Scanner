package com.blipblipcode.scanner.ui.camera

import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.blipblipcode.scanner.domain.useCase.IScanningBarcodeUseCase
import com.blipblipcode.scanner.domain.useCase.IStartCameraUseCase
import com.blipblipcode.scanner.ui.camera.states.CameraUiState
import com.google.mlkit.vision.barcode.common.Barcode
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {
    private val scanningBarcodeUseCase = mockk<dagger.Lazy<IScanningBarcodeUseCase>>()
    private val startCameraUseCase = mockk<dagger.Lazy<IStartCameraUseCase>>()
    private val scanningBarcodeUseCaseMock = mockk<IScanningBarcodeUseCase>()
    private val startCameraUseCaseMock = mockk<IStartCameraUseCase>()
    private lateinit var viewModel: CameraViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { scanningBarcodeUseCase.get() } returns scanningBarcodeUseCaseMock
        every { startCameraUseCase.get() } returns startCameraUseCaseMock

        viewModel = CameraViewModel(scanningBarcodeUseCase, startCameraUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should initialize uiState as Idle`() {
        assertEquals(CameraUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `should return correct type for QR Code format`() {
        /*GIVEN*/
        val format = Barcode.FORMAT_QR_CODE
        /*WHEN*/
        val type = viewModel.run {
            val method = this.javaClass.getDeclaredMethod("getType", Int::class.java)
            method.isAccessible = true
            method.invoke(this, format) as String
        }
        /*THEN*/
        assertEquals("QR Code", type)
    }

    @Test
    fun `should return Unknown for unsupported format`() {
        /*GIVEN*/
        val format = -1
        /*WHEN*/
        val type = viewModel.run {
            val method = this.javaClass.getDeclaredMethod("getType", Int::class.java)
            method.isAccessible = true
            method.invoke(this, format) as String
        }
        /*THEN*/
        assertEquals("Unknown", type)
    }

    @Test
    fun `should update preview and state when startCamera succeeds`() = runTest {
        /*GIVEN*/
        val previewView = mockk<PreviewView>()
        val barcode = mockk<Barcode> {
            every { displayValue } returns "123456"
            every { format } returns Barcode.FORMAT_QR_CODE
        }
        val imageProxy = mockk<ImageProxy>()
        val onCompleteMock = mockk<(String, String) -> Unit>(relaxed = true)
        val recognizerImageLambda = slot<(ImageProxy) -> Unit>()

        every { startCameraUseCaseMock.invoke(capture(recognizerImageLambda)) } returns Result.success(previewView)
        coEvery { scanningBarcodeUseCaseMock.invoke(imageProxy) } returns Result.success(barcode)

        /*WHEN*/
        viewModel.startCamera(onCompleteMock)
        recognizerImageLambda.captured.invoke(imageProxy)
        testDispatcher.scheduler.advanceUntilIdle()

        /*THEN*/
        assertEquals(previewView, viewModel.preview.value)
        assertEquals(CameraUiState.Scanning, viewModel.uiState.value)
        verify { onCompleteMock.invoke("123456", "QR Code") }
    }

    @Test
    fun `should update state when startCamera fails`() {
        /*GIVEN*/
        val error = Exception("Camera error")
        every { startCameraUseCaseMock.invoke(any()) } returns Result.failure(error)

        /*WHEN*/
        viewModel.startCamera { _, _ -> }

        /*THEN*/
        assertEquals(CameraUiState.Exception(error), viewModel.uiState.value)
    }

    @Test
    fun `should update errorException when scanningBarcode fails`() = runTest {
        /*GIVEN*/
        val previewView = mockk<PreviewView>()
        val imageProxy = mockk<ImageProxy>()
        val error = Exception("Barcode error")
        val recognizerImageLambda = slot<(ImageProxy) -> Unit>()

        every { startCameraUseCaseMock.invoke(capture(recognizerImageLambda)) } returns Result.success(previewView)
        coEvery { scanningBarcodeUseCaseMock.invoke(imageProxy) } returns Result.failure(error)

        /*WHEN*/
        viewModel.startCamera { _, _ -> }
        recognizerImageLambda.captured.invoke(imageProxy)
        testDispatcher.scheduler.advanceUntilIdle()

        /*THEN*/
        assertEquals(error, viewModel.errorException.value)
    }

    @Test
    fun `should not execute onComplete when barcode is null`() = runTest {
        /*GIVEN*/
        val previewView = mockk<PreviewView>()
        val imageProxy = mockk<ImageProxy>()
        val onCompleteMock = mockk<(String, String) -> Unit>(relaxed = true)
        val recognizerImageLambda = slot<(ImageProxy) -> Unit>()

        every { startCameraUseCaseMock.invoke(capture(recognizerImageLambda)) } returns Result.success(previewView)
        coEvery { scanningBarcodeUseCaseMock.invoke(imageProxy) } returns Result.success(null)

        /*WHEN*/
        viewModel.startCamera(onCompleteMock)
        recognizerImageLambda.captured.invoke(imageProxy)
        testDispatcher.scheduler.advanceUntilIdle()

        /*THEN*/
        verify(exactly = 0) { onCompleteMock.invoke(any(), any()) }
    }
}
