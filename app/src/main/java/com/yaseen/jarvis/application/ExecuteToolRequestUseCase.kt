package com.yaseen.jarvis.application

import com.yaseen.jarvis.domain.tool.ExecutionOutcome
import com.yaseen.jarvis.domain.tool.ToolRequest
import com.yaseen.jarvis.tools.ToolRegistry
import javax.inject.Inject

class ExecuteToolRequestUseCase @Inject constructor(
    private val toolRegistry: ToolRegistry
) {
    suspend operator fun invoke(request: ToolRequest): ExecutionOutcome =
        toolRegistry.evaluate(request)
}