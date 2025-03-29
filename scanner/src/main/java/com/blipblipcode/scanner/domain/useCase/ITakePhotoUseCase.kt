package com.blipblipcode.scanner.domain.useCase

import android.graphics.Bitmap

interface ITakePhotoUseCase {
    operator fun invoke(onCaptureSuccess: (Bitmap) -> Unit)
}