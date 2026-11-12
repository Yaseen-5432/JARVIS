package com.yaseen.jarvis.tools

import com.yaseen.jarvis.domain.action.ConfirmationToken
import com.yaseen.jarvis.domain.action.PendingAction
import com.yaseen.jarvis.domain.action.PendingActionStore
import com.yaseen.jarvis.domain.action.PermissionId
import com.yaseen.jarvis.domain.action.PermissionToken
import com.yaseen.jarvis.domain.tool.ConfirmationPolicy
import com.yaseen.jarvis.domain.tool.ExecutionOutcome
import com.yaseen.jarvis.domain.tool.ExecutionPolicy
import com.yaseen.jarvis.domain.tool.PermissionStateChecker
import com.yaseen.jarvis.domain.tool.Tool
import com.yaseen.jarvis.domain.tool.ToolRequest
import com.yaseen.jarvis.domain.tool.ToolResult
import com.yaseen.jarvis.domain.tool.ValidationResult
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExecutionPolicyImpl @Inject constructor(
    private val permissionStateChecker: PermissionStateChecker,
    private val pendingActionStore: PendingActionStore
) : ExecutionPolicy {

    override suspend fun evaluate(tool: Tool, request: ToolRequest): ExecutionOutcome {

        // Step 1: structural validation
        val validation = tool.validate(request)
        if (validation is ValidationResult.Invalid) {
            return ExecutionOutcome.Failed(ToolResult.Failure.ValidationFailed(validation.reason))
        }

        // Step 2: permission gate (always before confirmation)
        val missing = firstMissingPermission(tool)
        if (missing != null) {
            val pending = PendingAction.PendingPermission(
                token = PermissionToken(UUID.randomUUID().toString()),
                validatedRequest = request,
                permissionId = missing
            )
            pendingActionStore.savePendingPermission(pending)
            return ExecutionOutcome.NeedsPermission(pending)
        }

        // Step 3: confirmation gate
        if (tool.definition.confirmationPolicy == ConfirmationPolicy.ALWAYS) {
            val pending = PendingAction.PendingConfirmation(
                token = ConfirmationToken(UUID.randomUUID().toString()),
                validatedRequest = request
            )
            pendingActionStore.savePendingConfirmation(pending)
            return ExecutionOutcome.NeedsConfirmation(pending)
        }

        // Step 4: execute
        return ExecutionOutcome.Completed(tool.execute(request))
    }

    override suspend fun executeConfirmed(tool: Tool, request: ToolRequest): ExecutionOutcome {
        val validation = tool.validate(request)
        if (validation is ValidationResult.Invalid) {
            return ExecutionOutcome.Failed(ToolResult.Failure.ValidationFailed(validation.reason))
        }

        // Race safety: the permission may have been revoked since the prompt.
        val missing = firstMissingPermission(tool)
        if (missing != null) {
            return ExecutionOutcome.Failed(ToolResult.Failure.PermissionDenied(missing))
        }

        return ExecutionOutcome.Completed(tool.execute(request))
    }

    private fun firstMissingPermission(tool: Tool): PermissionId? =
        tool.definition.requiredPermissions
            .map { it.id }
            .firstOrNull { !permissionStateChecker.isGranted(it) }
}