package com.example.shoppingwithfriends.hilt

import android.content.Context
import androidx.room.Room
import com.example.shoppingwithfriends.data.source.local.ShoppingDao
import com.example.shoppingwithfriends.data.source.local.ShoppingDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): ShoppingDatabase {
        return Room.databaseBuilder(
                context,
                ShoppingDatabase::class.java,
                "shopping.db"
            ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideShoppingDao(db: ShoppingDatabase): ShoppingDao = db.shoppingDao()
}