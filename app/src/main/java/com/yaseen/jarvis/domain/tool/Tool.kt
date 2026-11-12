package com.yaseen.jarvis.domain.tool

interface Tool {

    val definition: ToolDefinition

    /** Does this tool handle this kind of request? Used by ToolRegistry to dispatch. */
    fun supports(request: ToolRequest): Boolean

    /** Is this request's data structurally valid? A separate step from execution (Architecture §10, step 1). */
    fun validate(request: ToolRequest): ValidationResult

    /** Actually perform the action. Only ever called once policy has allowed it. */
    suspend fun execute(request: ToolRequest): ToolResult<*>
}