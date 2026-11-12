package com.yaseen.jarvis.application

import com.yaseen.jarvis.domain.action.ConfirmationToken
import com.yaseen.jarvis.domain.action.PendingAction
import com.yaseen.jarvis.domain.action.PendingActionStore
import com.yaseen.jarvis.domain.action.PermissionToken
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory only, on purpose. If the process dies, every pending action is
 * lost and the user must ask again. We never persist a half-finished
 * sensitive action (Architecture §10, process-death rule).
 */
@Singleton
class InMemoryPendingActionStore @Inject constructor(
    private val clock: Clock
) : PendingActionStore {

    private data class Stamped<T>(val action: T, val savedAt: Instant)

    private val permissions =
        ConcurrentHashMap<PermissionToken, Stamped<PendingAction.PendingPermission>>()
    private val confirmations =
        ConcurrentHashMap<ConfirmationToken, Stamped<PendingAction.PendingConfirmation>>()

    override suspend fun savePendingPermission(action: PendingAction.PendingPermission) {
        purgeExpired()
        permissions[action.token] = Stamped(action, clock.instant())
    }

    // remove() is atomic: whoever removes the entry first wins, everyone else gets null.
    override suspend fun consumePendingPermission(
        token: PermissionToken
    ): PendingAction.PendingPermission? {
        val stamped = permissions.remove(token) ?: return null
        return stamped.action.takeUnless { isExpired(stamped.savedAt) }
    }

    override suspend fun savePendingConfirmation(action: PendingAction.PendingConfirmation) {
        purgeExpired()
        confirmations[action.token] = Stamped(action, clock.instant())
    }

    override suspend fun consumePendingConfirmation(
        token: ConfirmationToken
    ): PendingAction.PendingConfirmation? {
        val stamped = confirmations.remove(token) ?: return null
        return stamped.action.takeUnless { isExpired(stamped.savedAt) }
    }

    override suspend fun clear() {
        permissions.clear()
        confirmations.clear()
    }

    private fun isExpired(savedAt: Instant): Boolean =
        Duration.between(savedAt, clock.instant()) > TTL

    private fun purgeExpired() {
        permissions.entries.removeIf { isExpired(it.value.savedAt) }
        confirmations.entries.removeIf { isExpired(it.value.savedAt) }
    }

    private companion object {
        val TTL: Duration = Duration.ofMinutes(2)
    }
}