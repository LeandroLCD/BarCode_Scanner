package com.leandrolcd.barcode_scanner.domain

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import javax.inject.Inject

class CameraX @Inject constructor(
    private var context: Context,
    private var owner: LifecycleOwner,
    private val cameraExecutors: ExecutorService,
    private val dispatcher: CoroutineDispatcher
) {

   /* private val imageCapture = ImageCapture.Builder()
        .setTargetRotation(Surface.ROTATION_90)
        .build()*/

    //region Methods
    fun startBarcodeScannerPreviewView(scannerImage:(image: ImageProxy)->Unit): PreviewView {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        val previewView = PreviewView(context)

        previewView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            //region Properties


            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                //.setTargetRotation(Surface.ROTATION_90)
                .build()
            val preview = Preview.Builder().build().also {
                //it.targetRotation = Surface.ROTATION_90
                it.surfaceProvider = previewView.surfaceProvider
            }
            val camSelector =
                CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
            //endregion


            imageAnalysis.setAnalyzer(cameraExecutors) { imageProxy ->
                scannerImage(imageProxy)
                //  imageProxy.close()
            }

            try {
                cameraProviderFuture.get().unbindAll()
                cameraProviderFuture.get().bindToLifecycle(
                    owner,
                    camSelector,
                    preview,
                    //imageCapture,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return previewView
    }

    /*fun startCameraPreviewView(): PreviewView {

        //region Properties

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        val previewView = PreviewView(context)

        val preview = Preview.Builder().build().also {
            it.targetRotation = Surface.ROTATION_90
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val camSelector =
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        //endregion
        previewView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->



            try {
                cameraProviderFuture.get().unbindAll()
                cameraProviderFuture.get().bindToLifecycle(
                    owner,
                    camSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("TAG", "CameraX: $e")
            }
        }

        return previewView
    }*/

   /* fun capturePhoto() =owner.lifecycleScope.launch{


        imageCapture.takePicture(cameraExecutors, object :
            ImageCapture.OnImageCapturedCallback(), ImageCapture.OnImageSavedCallback {
            override fun onCaptureStarted() {
                TODO("Not yet implemented")
            }

            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                owner.lifecycleScope.launch {
                    saveMediaToStorage(
                        imageProxyToBitmap(image),
                        System.currentTimeMillis().toString()
                    )
                }
            }

            override fun onCaptureStarted() {
                TODO("Not yet implemented")
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                showLog("onCaptureSuccess: Uri  ${outputFileResults.savedUri}")
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                showLog("onCaptureSuccess: onError")
            }

            override fun onCaptureProcessProgressed(progress: Int) {
                TODO("Not yet implemented")
            }

            override fun onPostviewBitmapAvailable(bitmap: Bitmap) {
                TODO("Not yet implemented")
            }

            override fun onPostviewBitmapAvailable(bitmap: Bitmap) {
                TODO("Not yet implemented")
            }

            override fun onCaptureProcessProgressed(progress: Int) {
                TODO("Not yet implemented")
            }
        })


    }*/
    //endregion

    //region functions

    private suspend fun imageProxyToBitmap(image: ImageProxy): Bitmap =
        withContext(owner.lifecycleScope.coroutineContext) {
            val planeProxy = image.planes[0]
            val buffer: ByteBuffer = planeProxy.buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    private suspend fun saveMediaToStorage(bitmap: Bitmap, name: String) {
        withContext(dispatcher) {
            val filename = "$name.jpg"
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver?.also { resolver ->

                    val contentValues = ContentValues().apply {

                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_DCIM
                        )
                    }
                    val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    fos = imageUri?.let { with(resolver) { openOutputStream(it) } }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                val image = File(imagesDir, filename).also { fos = FileOutputStream(it) }
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    mediaScanIntent.data = Uri.fromFile(image)
                    context.sendBroadcast(mediaScanIntent)
                }
            }

            fos?.use {
                val success = async(dispatcher) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }
                if (success.await()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }


        }
    }
    //endregion
    private fun ImageProxy.toBitmapWithScreenDimensions(): Bitmap {

        val bitmap = this.convertToBitmap()
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val matrix = Matrix().apply {
            postRotate(90f)
            postScale(screenWidth.toFloat() / bitmap.width, screenHeight.toFloat() / bitmap.height)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

private fun Bitmap.rotateAndResize(width:Int, height:Int, angle: Float = 90f): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    val rotatedBitmap = Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    return Bitmap.createScaledBitmap(rotatedBitmap, width, height, true)
}

private fun ImageProxy.convertToBitmap(): Bitmap {

    val yBuffer = this.planes[0].buffer // Y
    val uBuffer = this.planes[1].buffer // U
    val vBuffer = this.planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(
        Rect(0, 0, yuvImage.width, yuvImage.height), 100,
        out
    )
    val imageBytes = out.toByteArray()

    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}