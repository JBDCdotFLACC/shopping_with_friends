package com.example.shoppingwithfriends.hilt

import android.content.Context
import com.example.shoppingwithfriends.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named

// AuthModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Named("webClientId")
    fun provideWebClientId(@ApplicationContext context: Context): String {
        return context.getString(R.string.default_web_client_id)
    }
}