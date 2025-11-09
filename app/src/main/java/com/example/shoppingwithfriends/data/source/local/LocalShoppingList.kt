package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_list"
)

data class LocalShoppingList (
    @PrimaryKey
    val id: String,
    var name: String,
    var date: Long,
)