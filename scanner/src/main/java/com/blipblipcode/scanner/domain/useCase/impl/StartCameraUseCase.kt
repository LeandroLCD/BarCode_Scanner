package com.blipblipcode.scanner.domain.useCase.impl

import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.blipblipcode.scanner.domain.useCase.IStartCameraUseCase
import com.blipblipcode.scanner.domain.ICameraRepository
import javax.inject.Inject

internal class StartCameraUseCase @Inject constructor(private val repository: ICameraRepository) :
    IStartCameraUseCase {
    override fun invoke(recognizerImage: (ImageProxy) -> Unit) = repository.startCameraPreviewView(recognizerImage)

}