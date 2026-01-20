package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "product"
)
data class LocalProduct (
    @PrimaryKey
    val id: String,
    var content: String,
    var parent: String,
    var isChecked: Boolean,
)
