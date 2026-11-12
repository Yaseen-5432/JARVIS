package com.yaseen.jarvis.application

import com.yaseen.jarvis.domain.action.ConfirmationToken
import com.yaseen.jarvis.domain.action.PendingActionStore
import com.yaseen.jarvis.domain.action.PermissionToken
import com.yaseen.jarvis.domain.tool.ExecutionOutcome
import com.yaseen.jarvis.domain.tool.ToolResult
import javax.inject.Inject

/** Discards a pending action without executing it. Consuming the token makes it unusable afterwards. */
class CancelPendingActionUseCase @Inject constructor(
    private val store: PendingActionStore
) {
    suspend fun cancelPermission(token: PermissionToken): ExecutionOutcome {
        store.consumePendingPermission(token)
        return ExecutionOutcome.Failed(ToolResult.Failure.UserCancelled)
    }

    suspend fun cancelConfirmation(token: ConfirmationToken): ExecutionOutcome {
        store.consumePendingConfirmation(token)
        return ExecutionOutcome.Failed(ToolResult.Failure.UserCancelled)
    }
}