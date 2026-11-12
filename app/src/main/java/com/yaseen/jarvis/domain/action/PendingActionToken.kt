package com.yaseen.jarvis.domain.action

/**
 * Two distinct token types instead of a shared String. This is what makes
 * "a permission grant can never be consumed as a confirmation" a compiler
 * guarantee rather than a naming convention (Architecture §10/§12).
 */
@JvmInline
value class PermissionToken(val value: String)

@JvmInline
value class ConfirmationToken(val value: String)