package com.yaseen.jarvis.tools

import com.yaseen.jarvis.domain.tool.ExecutionOutcome
import com.yaseen.jarvis.domain.tool.ExecutionPolicy
import com.yaseen.jarvis.domain.tool.Tool
import com.yaseen.jarvis.domain.tool.ToolRequest
import com.yaseen.jarvis.domain.tool.ToolResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRegistry @Inject constructor(
    private val tools: Set<@JvmSuppressWildcards Tool>,
    private val executionPolicy: ExecutionPolicy
) {

    /** Entry point for a fresh request. Fails closed if no tool is registered for it. */
    suspend fun evaluate(request: ToolRequest): ExecutionOutcome {
        val tool = findTool(request)
            ?: return ExecutionOutcome.Failed(ToolResult.Failure.UnsupportedOperation)
        return executionPolicy.evaluate(tool, request)
    }

    /** Entry point after an explicit user confirmation. Never reachable without a consumed token. */
    suspend fun executeConfirmed(request: ToolRequest): ExecutionOutcome {
        val tool = findTool(request)
            ?: return ExecutionOutcome.Failed(ToolResult.Failure.UnsupportedOperation)
        return executionPolicy.executeConfirmed(tool, request)
    }

    private fun findTool(request: ToolRequest): Tool? =
        tools.firstOrNull { it.supports(request) }
}