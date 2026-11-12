package com.yaseen.jarvis.domain.model


data class BatteryStatus(
    val levelPercent: Int,
    val isCharging: Boolean
)