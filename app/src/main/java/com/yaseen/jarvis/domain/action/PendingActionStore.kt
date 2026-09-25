package com.yaseen.jarvis.domain.action

interface PendingActionStore {

    suspend fun savePendingPermission(
        action: PendingAction.PendingPermission
    )

    suspend fun consumePendingPermission(
        token: String
    ): PendingAction.PendingPermission?

    suspend fun savePendingConfirmation(
        action: PendingAction.PendingConfirmation
    )

    suspend fun consumePendingConfirmation(
        token: String
    ): PendingAction.PendingConfirmation?

    suspend fun clear()
}