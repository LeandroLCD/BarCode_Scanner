package com.blipblipcode.scanner.data.core

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CameraExecutor {
    @Singleton
    @Provides
    fun providesCameraExecutor(): ExecutorService = Executors.newSingleThreadExecutor()
}