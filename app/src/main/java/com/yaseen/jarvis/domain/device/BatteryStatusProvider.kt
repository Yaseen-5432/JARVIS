package com.yaseen.jarvis.domain.device

import com.yaseen.jarvis.domain.model.BatteryStatus

/**
 * Domain-owned contract. The Android implementation lives in platform/battery,
 * so tools never touch Context directly.
 */
interface BatteryStatusProvider {
    /** Returns null if the system can't report a valid battery level. */
    fun read(): BatteryStatus?
}

