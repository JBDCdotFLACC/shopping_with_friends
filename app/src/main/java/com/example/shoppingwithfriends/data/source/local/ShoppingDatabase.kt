package com.example.shoppingwithfriends.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalProduct::class, LocalShoppingList::class], version = 1, exportSchema = false)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun shoppingDao(): ShoppingDao
}