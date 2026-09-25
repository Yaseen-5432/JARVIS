package com.example.jarvis.di

import android.content.Context
import androidx.room.Room
import com.example.jarvis.data.local.JarvisDatabase
import com.example.jarvis.data.local.conversation.ConversationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideJarvisDatabase(
        @ApplicationContext context: Context
    ): JarvisDatabase {
        return Room.databaseBuilder(
            context,
            JarvisDatabase::class.java,
            "jarvis_database"
        ).build()
    }

    @Provides
    fun provideConversationDao(
        database: JarvisDatabase
    ): ConversationDao {
        return database.conversationDao()
    }
}