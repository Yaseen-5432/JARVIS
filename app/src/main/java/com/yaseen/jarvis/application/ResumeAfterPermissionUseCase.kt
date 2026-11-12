package com.yaseen.jarvis.application

import com.yaseen.jarvis.domain.action.PendingActionStore
import com.yaseen.jarvis.domain.action.PermissionToken
import com.yaseen.jarvis.domain.tool.ExecutionOutcome
import com.yaseen.jarvis.domain.tool.PermissionStateChecker
import com.yaseen.jarvis.domain.tool.ToolResult
import com.yaseen.jarvis.tools.ToolRegistry
import javax.inject.Inject

/**
 * Called when the user answered the system permission dialog. It NEVER
 * executes the tool and NEVER treats the grant as confirmation. It only
 * re-runs the full pipeline on the same frozen request, which decides
 * independently whether confirmation is still required.
 */
class ResumeAfterPermissionUseCase @Inject constructor(
    private val store: PendingActionStore,
    private val permissionStateChecker: PermissionStateChecker,
    private val toolRegistry: ToolRegistry
) {
    suspend operator fun invoke(token: PermissionToken): ExecutionOutcome {
        val pending = store.consumePendingPermission(token)
            ?: return ExecutionOutcome.Failed(ToolResult.Failure.PendingActionUnavailable)

        // Ask the OS, not the dialog callback: the user may have denied it.
        if (!permissionStateChecker.isGranted(pending.permissionId)) {
            return ExecutionOutcome.Failed(
                ToolResult.Failure.PermissionDenied(pending.permissionId)
            )
        }

        // Same request, re-evaluated from the top. NLU is never re-run.
        return toolRegistry.evaluate(pending.validatedRequest)
    }
}