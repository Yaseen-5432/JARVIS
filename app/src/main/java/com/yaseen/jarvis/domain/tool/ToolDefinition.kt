package com.yaseen.jarvis.domain.tool

import com.yaseen.jarvis.domain.action.PermissionRequirement

data class ToolDefinition(
    val id: ToolId,
    val name: String,
    val sensitivity: ToolSensitivity,
    val confirmationPolicy: ConfirmationPolicy,
    val requiredPermissions: List<PermissionRequirement> = emptyList(),
    val requiresNetwork: Boolean = false
)