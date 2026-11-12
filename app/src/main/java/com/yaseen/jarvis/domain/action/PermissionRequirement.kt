package com.yaseen.jarvis.domain.action

/**
 * A permission a tool needs, plus the human-readable reason to show the
 * user before the system permission dialog appears (Architecture §28 —
 * the "why" must never be visual-only, it needs to be spoken too).
 */
data class PermissionRequirement(
    val id: PermissionId,
    val rationale: String
)