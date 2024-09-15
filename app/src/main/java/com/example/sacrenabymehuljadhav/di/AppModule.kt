package com.example.sacrenabymehuljadhav.di

import android.app.Application
import android.content.Context
import com.example.sacrenabymehuljadhav.ChatHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @ApplicationContext
    fun provideApplicationContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideChatHelper(@ApplicationContext context: Context): ChatHelper {
        return ChatHelper(context)
    }
}