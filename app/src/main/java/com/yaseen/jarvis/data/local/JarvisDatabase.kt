package com.yaseen.jarvis.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yaseen.jarvis.data.local.conversation.ConversationDao
import com.yaseen.jarvis.data.local.conversation.ConversationEntity

@Database(
    entities = [ConversationEntity::class],
    version = 1,
    exportSchema = true
)
abstract class JarvisDatabase : RoomDatabase() {

    abstract fun conversationDao(): ConversationDao
}