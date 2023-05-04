package com.leandrolcd.barcode_scanner.core.camera

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Module
@InstallIn(SingletonComponent::class)
object CameraExecutor {
    @Provides
    fun providesCameraExecutor(): ExecutorService = Executors.newSingleThreadExecutor()
}