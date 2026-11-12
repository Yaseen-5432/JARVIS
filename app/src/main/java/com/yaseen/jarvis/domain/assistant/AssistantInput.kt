package com.yaseen.jarvis.domain.assistant

enum class InputSource { TYPED, VOICE }

data class AssistantInput(
    val text: String,
    val source: InputSource = InputSource.TYPED
)