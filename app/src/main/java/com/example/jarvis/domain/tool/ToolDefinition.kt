package com.example.jarvis.domain.tool

data class ToolDefinition(
    val id: String,
    val name: String,
    val sensitivity: ToolSensitivity,
    val confirmationPolicy: ConfirmationPolicy
)