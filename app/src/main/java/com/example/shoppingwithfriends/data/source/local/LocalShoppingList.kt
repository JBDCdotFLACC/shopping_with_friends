package com.example.shoppingwithfriends.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(
    tableName = "shopping_list"
)

@Serializable
data class LocalShoppingList (
    @PrimaryKey
    val id: String = "",
    var name: String = "",
    var date: Long = 0L,
    var owner: String = "",
    @get:PropertyName("isDeleted")
    @set:PropertyName("isDeleted")
    @SerialName("isDeleted")
    var isDeleted: Boolean = false,
    @get:PropertyName("versionId")
    @set:PropertyName("versionId")
    var versionId: String = ""
)