package com.yaseen.jarvis.domain.action

import com.yaseen.jarvis.domain.tool.ToolRequest

sealed interface PendingAction {

    data class PendingPermission(
        val token: String,
        val validatedRequest: ToolRequest,
        val permissionId: PermissionId
    ) : PendingAction

    data class PendingConfirmation(
        val token: String,
        val validatedRequest: ToolRequest
    ) : PendingAction
}