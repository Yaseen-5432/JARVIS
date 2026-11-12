package com.yaseen.jarvis.domain.tool

/**
 * Step 1 of the execution pipeline (Architecture §10): does this request's
 * data even make sense, before we ask anything about permissions or
 * confirmation? E.g. SetAlarm(hour = 27) is structurally invalid.
 */
sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val reason: String) : ValidationResult
}