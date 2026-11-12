package com.yaseen.jarvis.domain.tool

import com.yaseen.jarvis.domain.action.PermissionId

sealed interface ToolResult<out T> {

    sealed interface Success<out T> : ToolResult<T> {
        data class Value<T>(val value: T) : Success<T>
        data class HandoffLaunched(val description: String) : Success<Nothing>
    }

    sealed interface Failure : ToolResult<Nothing> {
        data class ValidationFailed(val reason: String) : Failure
        data class PermissionDenied(val permissionId: PermissionId) : Failure
        data object UserCancelled : Failure
        data class NotFound(val what: String) : Failure
        data class IntentUnavailable(val action: String) : Failure
        data object UnsupportedOperation : Failure
        data class PlatformError(val cause: String) : Failure

        /** The pending token was unknown, already used, or expired. Never executes anything. */
        data object PendingActionUnavailable : Failure
    }
}