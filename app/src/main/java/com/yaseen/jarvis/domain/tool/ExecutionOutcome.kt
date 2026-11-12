package com.yaseen.jarvis.domain.tool

import com.yaseen.jarvis.domain.action.PendingAction

/**
 * What ToolRegistry.evaluate() actually returns. Notice NeedsConfirmation
 * and NeedsPermission are siblings of Completed and Failed — not sub-cases
 * of failure. They're pending, not broken (Architecture §17).
 */
sealed interface ExecutionOutcome {
    data class Completed(val result: ToolResult<*>) : ExecutionOutcome
    data class NeedsPermission(val pending: PendingAction.PendingPermission) : ExecutionOutcome
    data class NeedsConfirmation(val pending: PendingAction.PendingConfirmation) : ExecutionOutcome
    data class Failed(val failure: ToolResult.Failure) : ExecutionOutcome
}