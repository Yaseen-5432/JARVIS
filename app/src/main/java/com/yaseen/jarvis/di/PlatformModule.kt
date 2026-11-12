package com.yaseen.jarvis.di

import com.yaseen.jarvis.domain.device.BatteryStatusProvider
import com.yaseen.jarvis.domain.tool.PermissionStateChecker
import com.yaseen.jarvis.platform.battery.AndroidBatteryStatusProvider
import com.yaseen.jarvis.platform.permissions.AndroidPermissionStateChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PlatformModule {

    @Binds
    abstract fun bindBatteryStatusProvider(
        impl: AndroidBatteryStatusProvider
    ): BatteryStatusProvider

    @Binds
    abstract fun bindPermissionStateChecker(
        impl: AndroidPermissionStateChecker
    ): PermissionStateChecker
}