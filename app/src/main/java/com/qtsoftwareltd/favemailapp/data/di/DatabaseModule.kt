package com.qtsoftwareltd.favemailapp.data.di

import android.content.Context
import com.qtsoftwareltd.favemailapp.data.local.EmailDao
import com.qtsoftwareltd.favemailapp.data.local.EncryptedDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies
 * This module tells Hilt how to create database instances
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provide the encrypted database instance
     * Singleton ensures only one database instance exists
     */
    @Provides
    @Singleton
    fun provideEncryptedDatabase(
        @ApplicationContext context: Context
    ): EncryptedDB {
        return EncryptedDB.create(context)
    }
    
    /**
     * Provide the EmailDao from the database
     * Provides database instance for dependency injection
     */
    @Provides
    fun provideEmailDao(database: EncryptedDB): EmailDao {
        return database.emailDao()
    }
}

