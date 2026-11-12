package com.yaseen.jarvis.application

import com.yaseen.jarvis.domain.action.ConfirmationToken
import com.yaseen.jarvis.domain.action.PendingAction
import com.yaseen.jarvis.domain.action.PermissionId
import com.yaseen.jarvis.domain.action.PermissionToken
import com.yaseen.jarvis.domain.tool.ToolRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class InMemoryPendingActionStoreTest {

    private class MutableClock(private var now: Instant = Instant.parse("2026-01-01T00:00:00Z")) : Clock() {
        override fun getZone(): ZoneId = ZoneOffset.UTC
        override fun withZone(zone: ZoneId?): Clock = this
        override fun instant(): Instant = now
        fun advance(d: Duration) { now = now.plus(d) }
    }

    private val request = ToolRequest.SendSms("+920000000000", "hi")

    private fun confirmation(id: String) =
        PendingAction.PendingConfirmation(ConfirmationToken(id), request)

    private fun permission(id: String) =
        PendingAction.PendingPermission(PermissionToken(id), request, PermissionId.READ_CONTACTS)

    @Test
    fun confirmation_isSingleUse() = runBlocking {
        val store = InMemoryPendingActionStore(MutableClock())
        store.savePendingConfirmation(confirmation("c1"))

        assertNotNull(store.consumePendingConfirmation(ConfirmationToken("c1")))
        assertNull(store.consumePendingConfirmation(ConfirmationToken("c1")))
    }

    @Test
    fun permission_isSingleUse() = runBlocking {
        val store = InMemoryPendingActionStore(MutableClock())
        store.savePendingPermission(permission("p1"))

        assertNotNull(store.consumePendingPermission(PermissionToken("p1")))
        assertNull(store.consumePendingPermission(PermissionToken("p1")))
    }

    @Test
    fun staleConfirmation_isRejected() = runBlocking {
        val clock = MutableClock()
        val store = InMemoryPendingActionStore(clock)
        store.savePendingConfirmation(confirmation("c1"))

        clock.advance(Duration.ofMinutes(3))

        assertNull(store.consumePendingConfirmation(ConfirmationToken("c1")))
    }

    @Test
    fun freshConfirmation_isAccepted_beforeExpiry() = runBlocking {
        val clock = MutableClock()
        val store = InMemoryPendingActionStore(clock)
        store.savePendingConfirmation(confirmation("c1"))

        clock.advance(Duration.ofSeconds(90))

        assertEquals(request, store.consumePendingConfirmation(ConfirmationToken("c1"))?.validatedRequest)
    }

    @Test
    fun permissionAndConfirmation_withSameRawString_neverCollide() = runBlocking {
        val store = InMemoryPendingActionStore(MutableClock())
        store.savePendingPermission(permission("same"))

        // A confirmation token with the same raw string finds nothing.
        assertNull(store.consumePendingConfirmation(ConfirmationToken("same")))
        assertNotNull(store.consumePendingPermission(PermissionToken("same")))
    }

    @Test
    fun clear_dropsEverything() = runBlocking {
        val store = InMemoryPendingActionStore(MutableClock())
        store.savePendingConfirmation(confirmation("c1"))
        store.savePendingPermission(permission("p1"))

        store.clear()

        assertNull(store.consumePendingConfirmation(ConfirmationToken("c1")))
        assertNull(store.consumePendingPermission(PermissionToken("p1")))
    }
}