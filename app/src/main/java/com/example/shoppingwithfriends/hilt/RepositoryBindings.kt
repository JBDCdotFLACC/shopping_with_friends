package com.example.shoppingwithfriends.hilt

import com.example.shoppingwithfriends.data.ShoppingListRepository
import com.example.shoppingwithfriends.data.ShoppingListRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindings {
    @Binds
    @Singleton
    abstract fun bindRepo(impl: ShoppingListRepositoryImpl): ShoppingListRepository
}