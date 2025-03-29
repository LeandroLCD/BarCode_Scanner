package com.blipblipcode.scanner.data.core

import com.blipblipcode.scanner.data.repository.BarcodeScannerRepository
import com.blipblipcode.scanner.data.repository.CameraRepository
import com.blipblipcode.scanner.domain.IBarcodeScannerRepository
import com.blipblipcode.scanner.domain.ICameraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RepositoryModule {
    @Singleton
    @Provides
    internal fun provideBarcodeScannerRepository(repository: BarcodeScannerRepository): IBarcodeScannerRepository {
        return repository
    }

    @Singleton
    @Provides
    internal fun cameraRepository(repository: CameraRepository): ICameraRepository {
        return repository
    }

}