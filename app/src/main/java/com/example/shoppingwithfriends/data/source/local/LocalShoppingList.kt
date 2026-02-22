package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "shopping_list"
)

@Serializable
data class LocalShoppingList (
    @PrimaryKey
    val id: String,
    var name: String,
    var date: Long,
    var owner: String,
)