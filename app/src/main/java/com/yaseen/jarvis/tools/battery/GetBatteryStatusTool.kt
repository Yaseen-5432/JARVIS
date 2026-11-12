
package com.yaseen.jarvis.tools.battery

import com.yaseen.jarvis.domain.device.BatteryStatusProvider
import com.yaseen.jarvis.domain.tool.ConfirmationPolicy
import com.yaseen.jarvis.domain.tool.Tool
import com.yaseen.jarvis.domain.tool.ToolDefinition
import com.yaseen.jarvis.domain.tool.ToolId
import com.yaseen.jarvis.domain.tool.ToolRequest
import com.yaseen.jarvis.domain.tool.ToolResult
import com.yaseen.jarvis.domain.tool.ToolSensitivity
import com.yaseen.jarvis.domain.tool.ValidationResult
import javax.inject.Inject

class GetBatteryStatusTool @Inject constructor(
    private val batteryStatusProvider: BatteryStatusProvider
) : Tool {

    override val definition = ToolDefinition(
        id = ToolId.GET_BATTERY_STATUS,
        name = "Battery status",
        sensitivity = ToolSensitivity.LOW,
        confirmationPolicy = ConfirmationPolicy.NEVER
    )

    override fun supports(request: ToolRequest): Boolean =
        request is ToolRequest.GetBatteryStatus

    // This request carries no data, so there is nothing structural to validate.
    override fun validate(request: ToolRequest): ValidationResult = ValidationResult.Valid

    override suspend fun execute(request: ToolRequest): ToolResult<*> {
        if (request !is ToolRequest.GetBatteryStatus) {
            return ToolResult.Failure.UnsupportedOperation
        }
        val status = batteryStatusProvider.read()
            ?: return ToolResult.Failure.PlatformError("Battery level is unavailable")
        return ToolResult.Success.Value(status)
    }
}