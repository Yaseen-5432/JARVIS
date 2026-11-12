package com.yaseen.jarvis.domain.tool

interface ExecutionPolicy {

    /** The full ordered pipeline: validate, permission, confirmation, execute. */
    suspend fun evaluate(tool: Tool, request: ToolRequest): ExecutionOutcome

    /**
     * Called only after the user explicitly confirmed. Skips the confirmation
     * gate (already satisfied) but re-checks validation and permissions,
     * since a permission may have been revoked since the prompt was shown.
     */
    suspend fun executeConfirmed(tool: Tool, request: ToolRequest): ExecutionOutcome
}