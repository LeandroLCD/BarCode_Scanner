package com.blipblipcode.scanner.domain.useCase

import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView

interface IStartCameraUseCase {
    operator fun invoke(recognizerImage:(ImageProxy)->Unit): PreviewView
}