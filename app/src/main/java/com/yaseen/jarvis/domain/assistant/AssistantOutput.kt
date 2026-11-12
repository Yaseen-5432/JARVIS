package com.yaseen.jarvis.domain.assistant

import com.yaseen.jarvis.domain.tool.ToolRequest

enum class Confidence { HIGH, LOW }

sealed interface AssistantOutput {

    data class ToolCall(
        val request: ToolRequest,
        val confidence: Confidence
    ) : AssistantOutput

    data class Reply(
        val message: String
    ) : AssistantOutput

    data class ClarificationRequired(
        val message: String,
        val candidates: List<String> = emptyList()
    ) : AssistantOutput

    /** The engine understood the request but no matching tool exists. Different from ClarificationRequired — that's "I don't understand," this is "I understand but can't do it." */
    data class Unsupported(
        val message: String
    ) : AssistantOutput
}