package com.blipblipcode.scanner.domain.di


import com.blipblipcode.scanner.domain.useCase.IScanningBarcodeUseCase
import com.blipblipcode.scanner.domain.useCase.IStartCameraUseCase
import com.blipblipcode.scanner.domain.useCase.ITakePhotoUseCase
import com.blipblipcode.scanner.domain.useCase.impl.ScanningBarcodeUseCase
import com.blipblipcode.scanner.domain.useCase.impl.StartCameraUseCase
import com.blipblipcode.scanner.domain.useCase.impl.TakePhotoUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
internal abstract class UseCaseModule {
    @Binds
    internal abstract fun binsScanningBarcode(useCase: ScanningBarcodeUseCase): IScanningBarcodeUseCase
    @Binds
    internal abstract fun binsStartCamera(useCase: StartCameraUseCase): IStartCameraUseCase
    @Binds
    internal abstract fun binsTakePhoto(useCase: TakePhotoUseCase): ITakePhotoUseCase
}