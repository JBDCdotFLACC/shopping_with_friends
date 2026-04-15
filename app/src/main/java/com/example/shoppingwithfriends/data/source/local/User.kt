package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user"
)

data class User (
    @PrimaryKey
    val id: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var displayName: String = ""
)