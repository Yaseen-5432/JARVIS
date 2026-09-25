package com.yaseen.jarvis.domain.tool

sealed interface ToolResult {

    data class Success(
        val message: String
    ) : ToolResult

    data class HandoffLaunched(
        val message: String
    ) : ToolResult

    data class Failure(
        val message: String
    ) : ToolResult
}