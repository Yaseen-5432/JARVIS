package com.yaseen.jarvis.domain.action

interface PendingActionStore {

    suspend fun savePendingPermission(action: PendingAction.PendingPermission)
    suspend fun consumePendingPermission(token: PermissionToken): PendingAction.PendingPermission?

    suspend fun savePendingConfirmation(action: PendingAction.PendingConfirmation)
    suspend fun consumePendingConfirmation(token: ConfirmationToken): PendingAction.PendingConfirmation?

    suspend fun clear()
}