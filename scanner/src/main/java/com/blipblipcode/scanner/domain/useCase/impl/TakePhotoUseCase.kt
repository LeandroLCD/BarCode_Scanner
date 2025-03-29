package com.blipblipcode.scanner.domain.useCase.impl

import android.graphics.Bitmap
import com.blipblipcode.scanner.domain.useCase.ITakePhotoUseCase
import com.blipblipcode.scanner.domain.ICameraRepository
import javax.inject.Inject

internal class TakePhotoUseCase @Inject constructor(private val repository: ICameraRepository):
    ITakePhotoUseCase {
    override fun invoke(onCaptureSuccess: (Bitmap) -> Unit) = repository.takePhoto {
        onCaptureSuccess(it)
    }
}