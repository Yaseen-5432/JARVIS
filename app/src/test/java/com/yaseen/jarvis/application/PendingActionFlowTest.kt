package com.yaseen.jarvis.application

import com.yaseen.jarvis.domain.action.ConfirmationToken
import com.yaseen.jarvis.domain.action.PermissionId
import com.yaseen.jarvis.domain.action.PermissionRequirement
import com.yaseen.jarvis.domain.tool.ConfirmationPolicy
import com.yaseen.jarvis.domain.tool.ExecutionOutcome
import com.yaseen.jarvis.domain.tool.PermissionStateChecker
import com.yaseen.jarvis.domain.tool.Tool
import com.yaseen.jarvis.domain.tool.ToolDefinition
import com.yaseen.jarvis.domain.tool.ToolId
import com.yaseen.jarvis.domain.tool.ToolRequest
import com.yaseen.jarvis.domain.tool.ToolResult
import com.yaseen.jarvis.domain.tool.ToolSensitivity
import com.yaseen.jarvis.domain.tool.ValidationResult
import com.yaseen.jarvis.tools.ExecutionPolicyImpl
import com.yaseen.jarvis.tools.ToolRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock

class PendingActionFlowTest {

    private class MutablePermissions(var granted: Boolean) : PermissionStateChecker {
        override fun isGranted(permissionId: PermissionId) = granted
    }

    /** Needs READ_CONTACTS and always confirms. Records exactly what it executed. */
    private class RecordingSmsTool : Tool {
        val executed = mutableListOf<ToolRequest>()
        override val definition = ToolDefinition(
            id = ToolId.SEND_SMS,
            name = "Fake SMS",
            sensitivity = ToolSensitivity.HIGH,
            confirmationPolicy = ConfirmationPolicy.ALWAYS,
            requiredPermissions = listOf(
                PermissionRequirement(PermissionId.READ_CONTACTS, "to find the contact")
            )
        )
        override fun supports(request: ToolRequest) = request is ToolRequest.SendSms
        override fun validate(request: ToolRequest) = ValidationResult.Valid
        override suspend fun execute(request: ToolRequest): ToolResult<*> {
            executed += request
            return ToolResult.Success.HandoffLaunched("opened composer")
        }
    }

    private val sms = ToolRequest.SendSms(phoneNumber = "+920000000000", message = "I'll be late")

    private val permissions = MutablePermissions(granted = false)
    private val store = InMemoryPendingActionStore(Clock.systemUTC())
    private val tool = RecordingSmsTool()
    private val registry = ToolRegistry(setOf(tool), ExecutionPolicyImpl(permissions, store))

    private val execute = ExecuteToolRequestUseCase(registry)
    private val resume = ResumeAfterPermissionUseCase(store, permissions, registry)
    private val confirm = ConfirmPendingActionUseCase(store, registry)
    private val cancel = CancelPendingActionUseCase(store)

    /** Drives the realistic first-time path: ask, grant permission, arrive at the confirmation prompt. */
    private suspend fun reachConfirmation(): ConfirmationToken {
        val first = execute(sms) as ExecutionOutcome.NeedsPermission
        permissions.granted = true
        val second = resume(first.pending.token) as ExecutionOutcome.NeedsConfirmation
        return second.pending.token
    }

    @Test
    fun permissionGrant_leadsToConfirmation_neverToExecution() = runBlocking {
        val outcome = execute(sms)
        assertTrue(outcome is ExecutionOutcome.NeedsPermission)

        permissions.granted = true
        val resumed = resume((outcome as ExecutionOutcome.NeedsPermission).pending.token)

        assertTrue(resumed is ExecutionOutcome.NeedsConfirmation)
        assertTrue(tool.executed.isEmpty())
    }

    @Test
    fun permissionDenied_failsAndNeverExecutes() = runBlocking {
        val outcome = execute(sms) as ExecutionOutcome.NeedsPermission

        // The user denied it: still not granted.
        val resumed = resume(outcome.pending.token)

        assertEquals(
            ExecutionOutcome.Failed(ToolResult.Failure.PermissionDenied(PermissionId.READ_CONTACTS)),
            resumed
        )
        assertTrue(tool.executed.isEmpty())
    }

    @Test
    fun permissionToken_isSingleUse() = runBlocking {
        val outcome = execute(sms) as ExecutionOutcome.NeedsPermission
        permissions.granted = true

        resume(outcome.pending.token)
        val second = resume(outcome.pending.token)

        assertEquals(
            ExecutionOutcome.Failed(ToolResult.Failure.PendingActionUnavailable),
            second
        )
    }

    @Test
    fun confirm_executesTheExactValidatedRequest_exactlyOnce() = runBlocking {
        val token = reachConfirmation()

        val first = confirm(token)
        val second = confirm(token)

        assertTrue(first is ExecutionOutcome.Completed)
        assertEquals(listOf<ToolRequest>(sms), tool.executed)
        assertEquals(
            ExecutionOutcome.Failed(ToolResult.Failure.PendingActionUnavailable),
            second
        )
    }

    @Test
    fun confirm_afterPermissionRevoked_doesNotExecute() = runBlocking {
        val token = reachConfirmation()

        permissions.granted = false
        val outcome = confirm(token)

        assertEquals(
            ExecutionOutcome.Failed(ToolResult.Failure.PermissionDenied(PermissionId.READ_CONTACTS)),
            outcome
        )
        assertTrue(tool.executed.isEmpty())
    }

    @Test
    fun cancel_discardsThePendingAction_soConfirmCannotRunIt() = runBlocking {
        val token = reachConfirmation()

        val cancelled = cancel.cancelConfirmation(token)
        val confirmed = confirm(token)

        assertEquals(ExecutionOutcome.Failed(ToolResult.Failure.UserCancelled), cancelled)
        assertEquals(
            ExecutionOutcome.Failed(ToolResult.Failure.PendingActionUnavailable),
            confirmed
        )
        assertTrue(tool.executed.isEmpty())
    }

    @Test
    fun unregisteredRequest_failsClosed() = runBlocking {
        val emptyRegistry = ToolRegistry(emptySet(), ExecutionPolicyImpl(permissions, store))

        val outcome = ExecuteToolRequestUseCase(emptyRegistry)(ToolRequest.GetBatteryStatus)

        assertEquals(
            ExecutionOutcome.Failed(ToolResult.Failure.UnsupportedOperation),
            outcome
        )
    }
}