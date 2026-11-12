package com.yaseen.jarvis.platform.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.yaseen.jarvis.domain.device.BatteryStatusProvider
import com.yaseen.jarvis.domain.model.BatteryStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidBatteryStatusProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : BatteryStatusProvider {

    override fun read(): BatteryStatus? {
        // Passing a null receiver only reads the current sticky broadcast.
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return null

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        return BatteryStatus(
            levelPercent = level * 100 / scale,
            isCharging = charging
        )
    }
}