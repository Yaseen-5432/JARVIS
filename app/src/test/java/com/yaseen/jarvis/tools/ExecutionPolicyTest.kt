package com.yaseen.jarvis.tools

import com.yaseen.jarvis.domain.action.ConfirmationToken
import com.yaseen.jarvis.domain.action.PendingAction
import com.yaseen.jarvis.domain.action.PendingActionStore
import com.yaseen.jarvis.domain.action.PermissionId
import com.yaseen.jarvis.domain.action.PermissionRequirement
import com.yaseen.jarvis.domain.action.PermissionToken
import com.yaseen.jarvis.domain.device.BatteryStatusProvider
import com.yaseen.jarvis.domain.model.BatteryStatus
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
import com.yaseen.jarvis.tools.battery.GetBatteryStatusTool
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExecutionPolicyTest {

    // ---- Fakes -------------------------------------------------------------

    private class FakePermissions(var granted: Boolean) : PermissionStateChecker {
        override fun isGranted(permissionId: PermissionId) = granted
    }

    private class FakeStore : PendingActionStore {
        val permissions = mutableMapOf<PermissionToken, PendingAction.PendingPermission>()
        val confirmations = mutableMapOf<ConfirmationToken, PendingAction.PendingConfirmation>()

        override suspend fun savePendingPermission(action: PendingAction.PendingPermission) {
            permissions[action.token] = action
        }
        override suspend fun consumePendingPermission(token: PermissionToken) =
            permissions.remove(token)
        override suspend fun savePendingConfirmation(action: PendingAction.PendingConfirmation) {
            confirmations[action.token] = action
        }
        override suspend fun consumePendingConfirmation(token: ConfirmationToken) =
            confirmations.remove(token)
        override suspend fun clear() {
            permissions.clear(); confirmations.clear()
        }
    }

    /** Stands in for a sensitive tool: needs READ_CONTACTS and always confirms. */
    private class FakeSensitiveTool : Tool {
        var executeCount = 0
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
            executeCount++
            return ToolResult.Success.Value(Unit)
        }
    }

    private val sms = ToolRequest.SendSms(phoneNumber = "+920000000000", message = "hi")

    // ---- Tests -------------------------------------------------------------

    @Test
    fun safeTool_executesImmediately() = runBlocking {
        val provider = object : BatteryStatusProvider {
            override fun read() = BatteryStatus(levelPercent = 80, isCharging = false)
        }
        val policy = ExecutionPolicyImpl(FakePermissions(true), FakeStore())

        val outcome = policy.evaluate(GetBatteryStatusTool(provider), ToolRequest.GetBatteryStatus)

        assertTrue(outcome is ExecutionOutcome.Completed)
        val result = (outcome as ExecutionOutcome.Completed).result
        assertTrue(result is ToolResult.Success.Value<*>)
        assertEquals(
            BatteryStatus(80, false),
            (result as ToolResult.Success.Value<*>).value
        )
    }

    @Test
    fun missingPermission_returnsNeedsPermission_andDoesNotExecute() = runBlocking {
        val tool = FakeSensitiveTool()
        val store = FakeStore()
        val policy = ExecutionPolicyImpl(FakePermissions(false), store)

        val outcome = policy.evaluate(tool, sms)

        assertTrue(outcome is ExecutionOutcome.NeedsPermission)
        assertEquals(0, tool.executeCount)
        assertEquals(1, store.permissions.size)
    }

    @Test
    fun permissionGranted_stillRequiresConfirmation_neverExecutesDirectly() = runBlocking {
        val tool = FakeSensitiveTool()
        val store = FakeStore()
        val policy = ExecutionPolicyImpl(FakePermissions(true), store)

        val outcome = policy.evaluate(tool, sms)

        assertTrue(outcome is ExecutionOutcome.NeedsConfirmation)
        assertEquals(0, tool.executeCount)
        assertEquals(1, store.confirmations.size)
    }
}