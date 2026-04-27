package com.example.shoppingwithfriends.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LocalProduct::class, LocalShoppingList::class, PendingOp::class, User::class, Friendship::class, FriendRequest::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
}