package com.example.jarvis.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jarvis.data.local.conversation.ConversationDao
import com.example.jarvis.data.local.conversation.ConversationEntity

@Database(
    entities = [ConversationEntity::class],
    version = 1,
    exportSchema = true
)
abstract class JarvisDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
}