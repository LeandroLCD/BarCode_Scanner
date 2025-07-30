package com.blipblipcode.scanner.domain.useCase.impl

import androidx.camera.core.ImageProxy
import com.blipblipcode.scanner.domain.ICameraRepository
import com.blipblipcode.scanner.domain.useCase.IStartCameraUseCase
import javax.inject.Inject

internal class StartCameraUseCase @Inject constructor(private val repository: ICameraRepository) :
    IStartCameraUseCase {
    override fun invoke(recognizerImage: (ImageProxy) -> Unit) = repository.startCameraPreviewView(recognizerImage)

}