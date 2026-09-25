package com.example.jarvis.data.local.conversation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_messages")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val role: String,

    val content: String,

    val timestamp: Long
)