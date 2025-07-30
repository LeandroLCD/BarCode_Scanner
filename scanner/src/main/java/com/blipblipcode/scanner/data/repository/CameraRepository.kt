package com.blipblipcode.scanner.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blipblipcode.scanner.domain.ICameraRepository
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import javax.inject.Inject


internal class CameraRepository @Inject constructor(
    private var context: Context,
    private var owner: LifecycleOwner,
    private val cameraExecutors: ExecutorService
) : ICameraRepository {

    companion object {
        private var imageCapture = mutableStateOf<ImageCapture?>(null)
    }

    override fun startCameraPreviewView(recognizerImage: (ImageProxy) -> Unit): Result<PreviewView> {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val previewView = PreviewView(context)
        return try {
            previewView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->

                val preview = Preview.Builder().build().also {
                    //it.targetRotation = Surface.ROTATION_90
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    //.setTargetRotation(Surface.ROTATION_90)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutors) { imageProxy ->
                    recognizerImage(imageProxy)
                }

                imageCapture.value = ImageCapture.Builder()
                    //.setTargetAspectRatio(RATIO_4_3)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setJpegQuality(100)
                    .build()


                val camSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()


                cameraProviderFuture.get().unbindAll()
                cameraProviderFuture.get().bindToLifecycle(
                    owner,
                    camSelector,
                    imageAnalysis,
                    preview,
                    imageCapture.value
                )
            }
            Result.success(previewView)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun takePhoto(onCaptureSuccess: (Bitmap) -> Unit) {
        owner.lifecycleScope.launch {
            imageCapture.value?.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        owner.lifecycleScope.launch {
                            onCaptureSuccess(image.toBitmap())
                        }
                    }
                })
        }
    }


}