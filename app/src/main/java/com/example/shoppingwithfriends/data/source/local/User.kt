package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "user"
)

@Serializable
data class User (
    @PrimaryKey
    override val id: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var displayName: String = "",
    var friends : List<String> = listOf()
) : FireBaseModel