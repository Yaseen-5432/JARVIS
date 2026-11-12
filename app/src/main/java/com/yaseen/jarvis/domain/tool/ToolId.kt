package com.yaseen.jarvis.domain.tool

/**
 * The closed set of JARVIS V1 capabilities. Adding a tool means adding a
 * case here — that's intentional friction, not an oversight (Architecture
 * §2.5: "closed, explicit, named tool set").
 */
enum class ToolId {
    OPEN_APP,
    DIAL_CONTACT,
    SEND_SMS,
    SET_ALARM,
    CREATE_REMINDER,
    GET_BATTERY_STATUS,
    GET_WIFI_STATUS,
    WEB_SEARCH,
    YOUTUBE_SEARCH
}