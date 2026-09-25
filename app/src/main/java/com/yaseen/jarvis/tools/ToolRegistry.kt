package com.yaseen.jarvis.tools

import com.yaseen.jarvis.domain.tool.Tool
import com.yaseen.jarvis.domain.tool.ToolRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRegistry @Inject constructor(
    private val tools: Set<@JvmSuppressWildcards Tool>
) {

    fun findTool(request: ToolRequest): Tool? {
        return tools.firstOrNull { it.supports(request) }
    }
}