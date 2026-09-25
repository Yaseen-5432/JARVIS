package com.yaseen.jarvis.domain.assistant

import com.yaseen.jarvis.domain.tool.ToolRequest

sealed interface AssistantOutput {

    data class ToolRequestOutput(
        val request: ToolRequest
    ) : AssistantOutput

    data class Reply(
        val message: String
    ) : AssistantOutput

    data class ClarificationRequired(
        val message: String
    ) : AssistantOutput
}