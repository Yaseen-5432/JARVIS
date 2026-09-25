package com.example.jarvis.domain.tool

sealed interface ToolRequest {

    data class OpenApp(
        val packageName: String
    ) : ToolRequest

    data class DialContact(
        val contactId: String
    ) : ToolRequest

    data class SendSms(
        val phoneNumber: String,
        val message: String
    ) : ToolRequest

    data class SetAlarm(
        val hour: Int,
        val minute: Int
    ) : ToolRequest

    data class CreateReminder(
        val title: String,
        val triggerAtMillis: Long
    ) : ToolRequest

    data object GetBatteryStatus : ToolRequest

    data object GetWifiStatus : ToolRequest

    data class WebSearch(
        val query: String
    ) : ToolRequest

    data class YouTubeSearch(
        val query: String
    ) : ToolRequest
}