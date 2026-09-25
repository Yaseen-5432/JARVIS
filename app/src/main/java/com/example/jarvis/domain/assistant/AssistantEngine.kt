package com.example.jarvis.domain.assistant

interface AssistantEngine {

    suspend fun process(
        input: AssistantInput
    ): AssistantOutput
}