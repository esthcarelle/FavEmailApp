package com.qtsoftwareltd.favemailapp.data.di

import com.qtsoftwareltd.favemailapp.data.repository.EmailRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing repository dependencies
 * This module tells Hilt how to create repository instances
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * Provide the EmailRepository instance
     * Singleton ensures only one repository instance exists
     */
    @Provides
    @Singleton
    fun provideEmailRepository(
        emailDao: com.qtsoftwareltd.favemailapp.data.local.EmailDao
    ): EmailRepository {
        return EmailRepository(emailDao)
    }
}

