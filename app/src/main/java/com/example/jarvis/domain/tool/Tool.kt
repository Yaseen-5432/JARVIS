package com.example.jarvis.domain.tool

interface Tool {

    val definition: ToolDefinition

    fun supports(request: ToolRequest): Boolean

    suspend fun execute(request: ToolRequest): ToolResult
}