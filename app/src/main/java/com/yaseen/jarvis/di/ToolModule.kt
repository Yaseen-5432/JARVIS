package com.yaseen.jarvis.di

import com.yaseen.jarvis.domain.tool.ExecutionPolicy
import com.yaseen.jarvis.domain.tool.Tool
import com.yaseen.jarvis.tools.ExecutionPolicyImpl
import com.yaseen.jarvis.tools.battery.GetBatteryStatusTool
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ToolModule {

    @Binds
    abstract fun bindExecutionPolicy(impl: ExecutionPolicyImpl): ExecutionPolicy

    @Binds
    @IntoSet
    abstract fun bindGetBatteryStatusTool(tool: GetBatteryStatusTool): Tool
}