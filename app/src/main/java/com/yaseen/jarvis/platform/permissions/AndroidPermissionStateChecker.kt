package com.yaseen.jarvis.platform.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.yaseen.jarvis.domain.action.PermissionId
import com.yaseen.jarvis.domain.tool.PermissionStateChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidPermissionStateChecker @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionStateChecker {

    override fun isGranted(permissionId: PermissionId): Boolean {
        // POST_NOTIFICATIONS is a runtime permission only from API 33.
        // Below that there is nothing to grant, so asking the OS would
        // wrongly say "denied".
        if (permissionId == PermissionId.POST_NOTIFICATIONS &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) return true

        return ContextCompat.checkSelfPermission(
            context,
            AndroidPermissionMapper.toManifestPermission(permissionId)
        ) == PackageManager.PERMISSION_GRANTED
    }
}