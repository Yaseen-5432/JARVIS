package com.yaseen.jarvis.domain.assistant

interface AssistantEngine {

    suspend fun process(
        input: AssistantInput
    ): AssistantOutput
}