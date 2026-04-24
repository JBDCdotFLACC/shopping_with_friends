package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "friendship"
)

data class Friendship (
    @PrimaryKey
    override val id: String = "",
    var userId : String = "",
    var friendId : String = "",
    ) : FireBaseModel
