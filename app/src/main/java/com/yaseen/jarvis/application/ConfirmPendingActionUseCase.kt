package com.yaseen.jarvis.application

import com.yaseen.jarvis.domain.action.ConfirmationToken
import com.yaseen.jarvis.domain.action.PendingActionStore
import com.yaseen.jarvis.domain.tool.ExecutionOutcome
import com.yaseen.jarvis.domain.tool.ToolResult
import com.yaseen.jarvis.tools.ToolRegistry
import javax.inject.Inject

/**
 * The only path to executing a confirmation-required tool. It accepts a
 * ConfirmationToken only, so a PermissionToken cannot be passed here.
 */
class ConfirmPendingActionUseCase @Inject constructor(
    private val store: PendingActionStore,
    private val toolRegistry: ToolRegistry
) {
    suspend operator fun invoke(token: ConfirmationToken): ExecutionOutcome {
        // Single-use: a second tap finds nothing and executes nothing.
        val pending = store.consumePendingConfirmation(token)
            ?: return ExecutionOutcome.Failed(ToolResult.Failure.PendingActionUnavailable)

        // The exact request that was shown to the user, never re-derived.
        return toolRegistry.executeConfirmed(pending.validatedRequest)
    }
}