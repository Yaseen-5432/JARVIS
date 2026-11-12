package com.yaseen.jarvis.di

import com.yaseen.jarvis.application.InMemoryPendingActionStore
import com.yaseen.jarvis.domain.action.PendingActionStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {

    @Binds
    abstract fun bindPendingActionStore(impl: InMemoryPendingActionStore): PendingActionStore

    companion object {
        @Provides
        fun provideClock(): Clock = Clock.systemUTC()
    }
}