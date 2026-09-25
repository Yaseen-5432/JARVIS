package com.example.jarvis.data.local.conversation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Insert
    suspend fun insert(message: ConversationEntity)

    @Query(
        "SELECT * FROM conversation_messages " +
                "ORDER BY timestamp ASC, id ASC"
    )
    fun observeMessages(): Flow<List<ConversationEntity>>

    @Query("DELETE FROM conversation_messages")
    suspend fun deleteAll()
}