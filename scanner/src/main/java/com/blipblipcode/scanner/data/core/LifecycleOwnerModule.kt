package com.blipblipcode.scanner.data.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LifecycleOwnerModule {

    @Provides
    fun provideLifecycleOwner(): LifecycleOwner {
        return ProcessLifecycleOwner.get()

    }

}