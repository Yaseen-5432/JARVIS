package com.yaseen.jarvis.platform.permissions

import android.Manifest
import com.yaseen.jarvis.domain.action.PermissionId

/** The ONLY place a PermissionId becomes an android.Manifest string (Architecture §12). */
object AndroidPermissionMapper {
    fun toManifestPermission(id: PermissionId): String = when (id) {
        PermissionId.RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
        PermissionId.READ_CONTACTS -> Manifest.permission.READ_CONTACTS
        PermissionId.POST_NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
    }
}