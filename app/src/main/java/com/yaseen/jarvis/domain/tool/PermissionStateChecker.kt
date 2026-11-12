package com.yaseen.jarvis.domain.tool

import com.yaseen.jarvis.domain.action.PermissionId

/**
 * "Is this permission currently granted?" — kept as an interface in domain
 * so ExecutionPolicy never has to know about Context or
 * ActivityCompat.checkSelfPermission. The real implementation lives in
 * platform/permissions later (Architecture §12).
 */
interface PermissionStateChecker {
    fun isGranted(permissionId: PermissionId): Boolean
}